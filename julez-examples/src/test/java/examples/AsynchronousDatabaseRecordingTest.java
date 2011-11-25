package examples;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import test.JmsTestUtils;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.repository.JdbcEventRepository;
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
    }
    
    @After
    public void nuke() {
        JmsTestUtils.nukeBroker();
        JdbcTestUtils.nukeDatabase();        
    }
    
    @Test
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {        

        Scenario scenario = new DemoScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);        
        
        
        JdbcEventRepository eventRepository = new JdbcEventRepository(dataSource).ddl();
                
        JmsEventSource jmsEventSource = new JmsEventSource(connectionFactory).listen();
        jmsEventSource.register(eventRepository);
        
        JmsEventHandler jmsEventHandler = new JmsEventHandler(connectionFactory);        
        scenario.register(jmsEventHandler);        
                  
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(jmsEventHandler);
        runner.queue(scenarios).allocate(10, THREADS).go();
        
        jmsEventSource.shutdownGracefully();
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        ResultMonitor resultMonitor = new ResultMonitor();
        eventRepository.register(throughputMonitor).register(resultMonitor);
        eventRepository.raiseAllEvents();
                
        assertMinimumThroughput(100, throughputMonitor.getThroughput());
        assertPassMark(75, resultMonitor.getPercentage());
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
        }
        
    }
}
