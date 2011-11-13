package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.util.List;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.Embedder.RunningStoriesFailed;
import org.jbehave.core.embedder.SilentEmbedderMonitor;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.DefaultResultFactory;
import uk.co.acuminous.julez.recorder.JmsResultRecorder;
import uk.co.acuminous.julez.result.JdbcResultRepository;
import uk.co.acuminous.julez.result.JmsResultListener;
import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario2Steps;

public class TheWholeShebangTest extends WebTestCase {

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
    public void demonstrateARealisticPerformanceTest() {
        
        JmsResultRecorder recorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        JBehaveScenario scenario = new JBehaveScenario("scenario2.txt", recorder);
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        
        runner.run();
        recorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        assertMinimumThroughput(10, runner.actualThroughput());
        assertPassMark(90, recorder.percentage()); 

        resultRepository.dump();
    }

    class JBehaveScenario implements Scenario {

        private final String scenario;
        private final JmsResultRecorder recorder;

        public JBehaveScenario(String scenario, JmsResultRecorder recorder) {
            this.scenario = scenario;
            this.recorder = recorder;
        }

        public void execute() {
            Embedder embedder = new Embedder();
            embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
            embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
            embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), new Scenario2Steps(recorder)).createCandidateSteps());

            List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(TheWholeShebangTest.class), scenario, "");
            
            try {
                embedder.runStoriesAsPaths(storyPaths);
            } catch (RunningStoriesFailed e) {
                // Test probably finished leaving some stories queued
            }
        }
    }    
    
}
