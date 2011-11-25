package uk.co.acuminous.julez.event.source;

import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.plumbing.BaseEventPipe;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import uk.co.acuminous.julez.util.JmsHelper;

public class JmsEventSource extends BaseEventPipe implements MessageListener, Runnable {

    private final QueueConnection connection;
    private final String queueName;
    private QueueSession session;
    private Thread listenerThread;
    private long lastReceivedTimestamp = System.currentTimeMillis();    
    private long shutdownDelay = 10000;

    public JmsEventSource(QueueConnectionFactory connectionFactory) {
        this(connectionFactory, JmsEventHandler.DEFAULT_QUEUE_NAME);
    }

    public JmsEventSource(QueueConnectionFactory connectionFactory, String queueName) {
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;
    }    
    
    public JmsEventSource listen() {
        listenerThread = ConcurrencyUtils.start(this);
        return this;
    }
    
    public void setShutdownDelay(long duration, TimeUnit timeUnit) {
        this.shutdownDelay = MILLISECONDS.convert(duration, timeUnit);
    }
    
    public void run() {
        synchronized (this) {
            try {
                this.session = JmsHelper.registerListener(queueName, connection, this);
                wait();
            } catch (InterruptedException e) {
                // Meh
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })    
    public void onMessage(Message message) {
        try {
            lastReceivedTimestamp = System.currentTimeMillis();
            String json = JmsHelper.getText(message);
            String className = message.getStringProperty(JmsEventHandler.EVENT_CLASS);
            Class<Event> eventClass = (Class<Event>) Class.forName(className);
            onEvent(eventClass.newInstance().fromJson(json));
        } catch (Throwable t) {
            System.err.println(t);
        }
    }

    public void shutdownGracefully() {
        try {            
            while (System.currentTimeMillis() - lastReceivedTimestamp < shutdownDelay) {
                ConcurrencyUtils.sleep(1, SECONDS);
            }
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
        JmsHelper.close(session, connection);
    }

    @Override
    protected void finalize() {
        JmsHelper.close(session, connection);
    }
}
