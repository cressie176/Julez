package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import test.JmsTestUtils;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.repository.JdbcEventRepository;
import uk.co.acuminous.julez.event.source.JmsEventSource;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioSource;
import uk.co.acuminous.julez.test.WebTestCase;
import uk.co.acuminous.julez.util.PerformanceAssert;
import uk.co.acuminous.julez.util.ScenarioRepeater;
import examples.jbehave.Scenario2Steps;


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

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps());        
                
        JdbcEventRepository eventRepository = new JdbcEventRepository(dataSource).ddl();
        
        JmsEventSource asynchronousListener = new JmsEventSource(connectionFactory).listen();
        asynchronousListener.registerEventHandler(eventRepository);
        
        JmsEventHandler jmsSender = new JmsEventHandler(connectionFactory);               
        scenario.registerEventHandler(jmsSender);        
        
        ScenarioSource scenarios = ScenarioRepeater.getScenarios(scenario, 100);  
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.registerEventHandler(jmsSender);
        runner.queue(scenarios).run();
        
        asynchronousListener.shutdownGracefully();

        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        eventRepository.registerEventHandler(throughputMonitor);
        eventRepository.raiseAllEvents();
                
        PerformanceAssert.assertMinimumThroughput(10, throughputMonitor.getThroughput());
    }
}
