package uk.co.acuminous.julez.event.source;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;
import uk.co.acuminous.julez.marshalling.EventUnmarshaller;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import uk.co.acuminous.julez.util.JmsHelper;

public class JmsEventSource extends PassThroughPipe implements MessageListener, Runnable {

    private final QueueConnection connection;
    private final String queueName;
    private QueueSession session;
    private Thread listenerThread;
    private long lastReceivedTimestamp = System.currentTimeMillis();    
    private long shutdownDelay = 10000;
    private final EventUnmarshaller unmarshaller;

    public JmsEventSource(QueueConnectionFactory connectionFactory, EventUnmarshaller unmarshaller) {
        this(connectionFactory, JmsEventHandler.DEFAULT_QUEUE_NAME, unmarshaller);
    }

    public JmsEventSource(QueueConnectionFactory connectionFactory, String queueName, EventUnmarshaller unmarshaller) {
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;
        this.unmarshaller = unmarshaller;        
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
    public void onMessage(Message message) {
        try {
            lastReceivedTimestamp = System.currentTimeMillis();
            String text = JmsHelper.getText(message);
            onEvent(unmarshaller.unmarshall(text));
        } catch (Throwable t) {
            System.err.println(t);
        }
    }

    public void shutdownWhenEmpty() {
        try {            
            while (System.currentTimeMillis() - lastReceivedTimestamp < shutdownDelay) {
                ConcurrencyUtils.sleep(100, MILLISECONDS);
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
