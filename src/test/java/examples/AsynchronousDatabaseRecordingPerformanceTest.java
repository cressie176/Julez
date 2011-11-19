package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.repository.ScenarioEventJdbcRepository;
import uk.co.acuminous.julez.event.repository.ScenarioEventJmsListener;
import uk.co.acuminous.julez.event.repository.ScenarioEventJmsSender;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario2Steps;


public class AsynchronousDatabaseRecordingPerformanceTest extends WebTestCase {

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
                
        ScenarioEventJdbcRepository repository = new ScenarioEventJdbcRepository(dataSource).ddl();
        
        ScenarioEventJmsListener asynchronousListener = new ScenarioEventJmsListener(connectionFactory).listen();
        asynchronousListener.registerEventHandler(repository);
        
        ScenarioEventJmsSender jmsSender = new ScenarioEventJmsSender(connectionFactory);               
        scenario.registerEventHandler(jmsSender);
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);  
        
        new ConcurrentScenarioRunner().queue(scenarios).run();
        
        asynchronousListener.shutdownGracefully();
        
        assertEquals(200, repository.count());                        
    }
}
