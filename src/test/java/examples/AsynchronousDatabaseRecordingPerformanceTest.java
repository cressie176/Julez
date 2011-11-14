package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.DefaultResultFactory;
import uk.co.acuminous.julez.recorder.JmsResultRecorder;
import uk.co.acuminous.julez.result.JdbcResultRepository;
import uk.co.acuminous.julez.result.JmsResultListener;
import uk.co.acuminous.julez.result.ResultStatus;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario2Steps;


public class AsynchronousDatabaseRecordingPerformanceTest extends WebTestCase {

    private ActiveMQConnectionFactory connectionFactory;
    private DataSource dataSource;
    private JdbcResultRepository resultRepository;
    private JmsResultListener resultListener;    
    
    @Before
    public void init() throws Exception {                
        TestUtils.createBroker();

        connectionFactory = TestUtils.getConnectionFactory();        
        dataSource = TestUtils.getDataSource();
        
        resultRepository = new JdbcResultRepository(dataSource).ddl();        
        resultListener = new JmsResultListener(connectionFactory, resultRepository).listen();        
    }
    
    @After
    public void nuke() {
        TestUtils.nukeBroker();
        TestUtils.nukeDatabase();        
    }
    
    @Test    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {
        
        JmsResultRecorder resultRecorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps(resultRecorder));        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);               
        
        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios); 
        runner.run();
        
        resultRecorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        resultRepository.dump(ResultStatus.FAIL);
                        
        assertMinimumThroughput(5, runner.throughput());
        assertPassMark(95, resultRecorder.percentage()); 
    }
}
