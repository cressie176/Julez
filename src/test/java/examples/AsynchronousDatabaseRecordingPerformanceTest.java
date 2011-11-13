package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.DefaultResultFactory;
import uk.co.acuminous.julez.recorder.JmsResultRecorder;
import uk.co.acuminous.julez.result.JdbcResultRepository;
import uk.co.acuminous.julez.result.JmsResultListener;
import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario2Steps;

public class AsynchronousDatabaseRecordingPerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;
    
    private ActiveMQConnectionFactory connectionFactory;
    private DataSource dataSource;
    private JdbcResultRepository resultRepository;
    private JmsResultListener resultListener;    
    
    @Before
    public void init() throws Exception {                
        TestUtils.createBroker();

        connectionFactory = TestUtils.getConnectionFactory();        
        dataSource = TestUtils.getDataSource();
        
        resultRepository = new JdbcResultRepository(dataSource);
        resultRepository.ddl(); 
        
        resultListener = new JmsResultListener(connectionFactory, resultRepository).listen();
    }
    
    @After
    public void nuke() {
        TestUtils.nukeDatabase();
        TestUtils.nukeBroker();
    }
    
    @Test(timeout=TEST_DURATION * 5000)
    public void demonstrateHowToRecordScenarioResultsAsynchronouslyToADatabase() {
        
        JmsResultRecorder recorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        JBehaveScenario scenario = new JBehaveScenario("scenario2.txt", new Scenario2Steps(recorder));
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        
        runner.run();
        recorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        assertMinimumThroughput(10, runner.actualThroughput());
        assertPassMark(90, recorder.percentage()); 

        resultRepository.dump();
    }       
}
