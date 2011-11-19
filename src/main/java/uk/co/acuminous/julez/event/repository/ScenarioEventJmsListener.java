package uk.co.acuminous.julez.event.repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventSource;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import uk.co.acuminous.julez.util.JmsHelper;

public class ScenarioEventJmsListener implements MessageListener, Runnable, EventSource {

    private final QueueConnection connection;
    private final String queueName;
    private QueueSession session;
    private long lastReceivedTimestamp = System.currentTimeMillis();
    private Thread listenerThread;
    private Set<EventHandler> handlers = new HashSet<EventHandler>();

    public ScenarioEventJmsListener(QueueConnectionFactory connectionFactory) {
        this(connectionFactory, ScenarioEventJmsSender.DEFAULT_QUEUE_NAME);
    }

    public ScenarioEventJmsListener(QueueConnectionFactory connectionFactory, String queueName) {
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;
    }

    @Override
    public void registerEventHandler(EventHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));     
    }        
    
    public ScenarioEventJmsListener listen() {
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
        lastReceivedTimestamp = System.currentTimeMillis();
        String json = JmsHelper.getText(message);
        ScenarioEvent event = ScenarioEvent.fromJson(json);
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
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
