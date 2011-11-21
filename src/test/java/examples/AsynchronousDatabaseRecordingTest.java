package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.async.JmsEventListener;
import uk.co.acuminous.julez.event.async.JmsEventSender;
import uk.co.acuminous.julez.event.filter.EventClassFilter;
import uk.co.acuminous.julez.event.repository.JdbcScenarioEventRepository;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.source.ScenarioSource;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario2Steps;


public class AsynchronousDatabaseRecordingTest extends WebTestCase {

    private ActiveMQConnectionFactory connectionFactory;
    private DataSource dataSource;
    
    @Before
    public void init() throws Exception {                
        TestUtils.createBroker();

        connectionFactory = TestUtils.getConnectionFactory();        
        dataSource = TestUtils.getDataSource();        
    }
    
    @After
    public void nuke() {
        TestUtils.nukeBroker();
        TestUtils.nukeDatabase();        
    }
    
    @Test
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {        

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps());        
                
        JdbcScenarioEventRepository scenarioEventRepository = new JdbcScenarioEventRepository(dataSource).ddl();
        EventClassFilter<ScenarioEvent> scenarioEventFilter = new EventClassFilter<ScenarioEvent>(ScenarioEvent.class);
        scenarioEventFilter.registerEventHandler(scenarioEventRepository);
        
        JmsEventListener asynchronousListener = new JmsEventListener(connectionFactory).listen();
        asynchronousListener.registerEventHandler(scenarioEventFilter);
        
        JmsEventSender jmsSender = new JmsEventSender(connectionFactory);               
        scenario.registerEventHandler(jmsSender);
        
        ScenarioSource scenarios = TestUtils.getScenarios(scenario, 100);  
        
        new ConcurrentScenarioRunner().queue(scenarios).run();
        
        asynchronousListener.shutdownGracefully();
        
        assertEquals(200, scenarioEventRepository.count());                        
    }
}
