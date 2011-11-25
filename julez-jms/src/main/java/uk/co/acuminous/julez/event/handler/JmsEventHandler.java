package uk.co.acuminous.julez.event.handler;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventMarshaller;
import uk.co.acuminous.julez.util.JmsHelper;

public class JmsEventHandler implements EventHandler {

    public static final String EVENT_TYPE = "EventType";
    public static final String EVENT_CLASS = "EventClass";
    public static final String EVENT_TIMESTAMP = "EventTimestamp";
    public static final String EVENT_CORRELATION_ID = "EventCorrelationId";
    
    public static final String DEFAULT_QUEUE_NAME = "julez";
    
    private final QueueConnection connection;
    private final String queueName;
    private final EventMarshaller marshaller;
    
    public JmsEventHandler(QueueConnectionFactory connectionFactory, EventMarshaller marshaller) {  
        this(connectionFactory, DEFAULT_QUEUE_NAME, marshaller);
    }
        
    public JmsEventHandler(QueueConnectionFactory connectionFactory, String queueName, EventMarshaller marshaller) {        
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;
        this.marshaller = marshaller;        
    }

    @Override
    public void onEvent(Event event) {
        QueueSession session = null;
        try {
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueSender sender = session.createSender(session.createQueue(queueName));
            
            String text = marshaller.marshal(event);
            
            TextMessage msg = session.createTextMessage(text);
            
            msg.setStringProperty(EVENT_CLASS, event.getClass().getName());
            msg.setStringProperty(EVENT_TYPE, event.getType());
            msg.setLongProperty(EVENT_TIMESTAMP, event.getTimestamp());
            msg.setStringProperty(EVENT_CORRELATION_ID, event.getCorrelationId());
            
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
