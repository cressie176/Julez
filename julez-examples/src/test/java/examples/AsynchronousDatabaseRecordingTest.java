package examples;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import test.JmsTestUtils;
import uk.co.acuminous.julez.event.handler.JdbcEventHandler;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.marshaller.JsonEventMarshaller;
import uk.co.acuminous.julez.event.source.JdbcEventRepository;
import uk.co.acuminous.julez.event.source.JmsEventSource;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
import uk.co.acuminous.julez.test.WebTestCase;


public class AsynchronousDatabaseRecordingTest extends WebTestCase {

    private ActiveMQConnectionFactory connectionFactory;
    private DataSource dataSource;
    
    @Before
    public void init() throws Exception {                
        JmsTestUtils.createBroker();

        connectionFactory = JmsTestUtils.getConnectionFactory();        
        dataSource = JdbcTestUtils.getDataSource();    
        JdbcTestUtils.ddl();
    }
    
    @After
    public void nuke() {
        JmsTestUtils.nukeBroker();
        JdbcTestUtils.nukeDatabase();        
    }
    
    @Test
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {        

        JdbcEventRepository jdbcEventSource = new JdbcEventRepository(dataSource);                
        JdbcEventHandler jdbcEventHandler = new JdbcEventHandler(dataSource);   
        
        JmsEventSource jmsEventSource = new JmsEventSource(connectionFactory);
        jmsEventSource.register(jdbcEventHandler);        
        jmsEventSource.listen();
        
        JmsEventHandler jmsEventHandler = new JmsEventHandler(connectionFactory, new JsonEventMarshaller());
        
        Scenario scenario = new DemoScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);              
        scenario.register(jmsEventHandler);                                       
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(jmsEventHandler);
        runner.queue(scenarios).allocate(10, THREADS).go();
        
        jmsEventSource.shutdownGracefully();

        assertEquals(302, jdbcEventSource.count());
    }
    
    class DemoScenario extends BaseScenario {
        
        private AtomicInteger counter = new AtomicInteger();
        
        @Override public void run() {
            onEvent(eventFactory.begin());
            
            if (counter.getAndIncrement() % 4 == 0) {
                onEvent(eventFactory.fail());
            } else {
                onEvent(eventFactory.pass());
            }
            
            onEvent(eventFactory.end());
        }
        
    }
}
