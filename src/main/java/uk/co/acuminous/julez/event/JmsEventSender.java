package uk.co.acuminous.julez.event;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import uk.co.acuminous.julez.util.JmsHelper;

public class JmsEventSender implements EventHandler {

    public static final String EVENT_TYPE = "EventType";
    public static final String EVENT_CLASS = "EventClass";
    public static final String DEFAULT_QUEUE_NAME = "julez";
    private final QueueConnection connection;
    private final String queueName;
    
    public JmsEventSender(QueueConnectionFactory connectionFactory) {  
        this(connectionFactory, DEFAULT_QUEUE_NAME);
    }
        
    public JmsEventSender(QueueConnectionFactory connectionFactory, String queueName) {        
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;        
    }

    @Override
    public void onEvent(Event<?> event) {
        QueueSession session = null;
        try {
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueSender sender = session.createSender(session.createQueue(queueName));
            TextMessage msg = session.createTextMessage(event.toJson());
            msg.setStringProperty(EVENT_CLASS, event.getClass().getName());
            msg.setStringProperty(EVENT_TYPE, event.getType());
            sender.send(msg);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            JmsHelper.close(session);
        }
    }
    
    public void shutdown() {
        JmsHelper.close(connection);        
    }
    
    @Override
    protected void finalize() {
        JmsHelper.close(connection);
    } 
}
