package uk.co.acuminous.julez.test;

import javax.jms.QueueConnectionFactory;
import javax.sql.DataSource;

import org.junit.After;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.source.JdbcEventRepository;
import uk.co.acuminous.julez.event.source.JmsEventSource;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.mapper.TwoWayMapper;
import uk.co.acuminous.julez.marshalling.json.JsonEventTranslator;
import uk.co.acuminous.julez.transformer.DefaultColumnNameTransformer;

public class EnterpriseTest {

    protected QueueConnectionFactory connectionFactory;
    protected DataSource dataSource;
    protected JmsEventSource jmsEventSource;
    protected JmsEventHandler jmsEventHandler;
    protected JdbcEventRepository jdbcEventRepository;
    
    @After
    public void nuke() {
        JmsTestUtils.nukeBroker();
        JdbcTestUtils.nukeDatabase();        
    }      

    protected void initJmsInfrastructure() {
                
        JmsTestUtils.createBroker();

        connectionFactory = JmsTestUtils.getConnectionFactory();        
                
        // The json event translator marshalls / unmarshalls the event to and from json
        JsonEventTranslator marshaller = new JsonEventTranslator();
        
        // The JMS event handler writes events asynchronously to the queue        
        jmsEventHandler = new JmsEventHandler(connectionFactory, marshaller);

        // The JMS event source re-raises events pulled asynchronously from the queue        
        jmsEventSource = new JmsEventSource(connectionFactory, marshaller);
        jmsEventSource.listen();        
    }

    protected void initDatabaseInfrastructure() {
        
        dataSource = JdbcTestUtils.getDataSource(); 
        
        createEventTable();
        
        // The column mapper enables mapping from event data property names to jdbc column names
        TwoWayMapper columnMapper = getColumnMapper();        
        
        // The JDBC event repository can be queried for events, or asked to re-raise them        
        jdbcEventRepository = new JdbcEventRepository(dataSource, columnMapper);        
    }

    protected void createEventTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute(
            "CREATE TABLE event (" +
            "id VARCHAR(36), " +
            "timestamp VARCHAR(255), " +
            "type VARCHAR(255), " +
            "PRIMARY KEY (id)" +
        ")");
    }

    protected TransformingMapper getColumnMapper() {
        return new TransformingMapper(new DefaultColumnNameTransformer(), Event.ID, Event.TIMESTAMP, Event.TYPE);
    }      
    
}
