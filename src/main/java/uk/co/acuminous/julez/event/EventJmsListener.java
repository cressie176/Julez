package uk.co.acuminous.julez.event;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import uk.co.acuminous.julez.util.JmsHelper;

public class EventJmsListener extends BaseEventSource implements MessageListener, Runnable {

    private final QueueConnection connection;
    private final String queueName;
    private QueueSession session;
    private long lastReceivedTimestamp = System.currentTimeMillis();
    private Thread listenerThread;

    public EventJmsListener(QueueConnectionFactory connectionFactory) {
        this(connectionFactory, EventJmsSender.DEFAULT_QUEUE_NAME);
    }

    public EventJmsListener(QueueConnectionFactory connectionFactory, String queueName) {
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;
    }    
    
    public EventJmsListener listen() {
        listenerThread = ConcurrencyUtils.start(this);
        return this;
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
            String json = JmsHelper.getText(message);
            ScenarioEvent event = ScenarioEvent.fromJson(json);
            raise(event);
        } catch (Throwable t) {
            System.err.println(t);
        }
    }

    public void shutdownGracefully() {
        try {
            while (System.currentTimeMillis() - lastReceivedTimestamp < 10000) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Meh
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
