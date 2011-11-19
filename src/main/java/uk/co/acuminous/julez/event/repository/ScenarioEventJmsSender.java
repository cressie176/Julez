package uk.co.acuminous.julez.event.repository;

import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.util.JmsHelper;

public class ScenarioEventJmsSender implements EventHandler {

    public static final String DEFAULT_QUEUE_NAME = "julez";
    private final QueueConnection connection;
    private final String queueName;
    
    public ScenarioEventJmsSender(QueueConnectionFactory connectionFactory) {  
        this(connectionFactory, DEFAULT_QUEUE_NAME);
    }
        
    public ScenarioEventJmsSender(QueueConnectionFactory connectionFactory, String queueName) {        
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;        
    }

    @Override
    public void onEvent(Event event) {
        JmsHelper.send(connection, queueName, event.toJson());
    } 
    
    public void shutdown() {
        JmsHelper.close(connection);        
    }
    
    @Override
    protected void finalize() {
        JmsHelper.close(connection);
    } 
}
