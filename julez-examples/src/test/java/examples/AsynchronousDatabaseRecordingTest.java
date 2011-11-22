package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import test.JmsTestUtils;
import uk.co.acuminous.julez.event.filter.EventClassFilter;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.repository.JdbcScenarioEventRepository;
import uk.co.acuminous.julez.event.source.JmsEventSource;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.source.ScenarioSource;
import uk.co.acuminous.julez.test.WebTestCase;
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
                
        JdbcScenarioEventRepository scenarioEventRepository = new JdbcScenarioEventRepository(dataSource).ddl();
        EventClassFilter<ScenarioEvent> scenarioEventFilter = new EventClassFilter<ScenarioEvent>(ScenarioEvent.class);
        scenarioEventFilter.registerEventHandler(scenarioEventRepository);
        
        JmsEventSource asynchronousListener = new JmsEventSource(connectionFactory).listen();
        asynchronousListener.registerEventHandler(scenarioEventFilter);
        
        JmsEventHandler jmsSender = new JmsEventHandler(connectionFactory);               
        scenario.registerEventHandler(jmsSender);
        
        ScenarioSource scenarios = ScenarioRepeater.getScenarios(scenario, 100);  
        
        new ConcurrentScenarioRunner().queue(scenarios).run();
        
        asynchronousListener.shutdownGracefully();
        
        assertEquals(200, scenarioEventRepository.count());          
    }
}
