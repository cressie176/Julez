package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.PerformanceAssert.assertMaxFailures;
import static uk.co.acuminous.julez.PerformanceAssert.assertMinPasses;
import static uk.co.acuminous.julez.PerformanceAssert.assertThroughput;

import java.util.List;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.SilentEmbedderMonitor;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.Test;

import uk.co.acuminous.julez.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.InMemoryResultRecorder;
import uk.co.acuminous.julez.ResultRecorder;
import uk.co.acuminous.julez.Scenario;

public class JBehavePerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemSupportsTheRequiredNumberOfJBehaveScenariosPerSecond() {

        ResultRecorder recorder = new InMemoryResultRecorder();
        JBehaveScenario scenario = new JBehaveScenario("scenario1.txt", recorder);
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.useNumberOfWorkers(15);
        concurrentTestRunner.run();

        assertMinPasses(1, recorder.successCount());
        assertMaxFailures(5, recorder.failureCount());
        assertThroughput(20, concurrentTestRunner.actualThroughput());
    }

    class JBehaveScenario implements Scenario {

        ResultRecorder recorder;
        private final String scenario;

        public JBehaveScenario(String scenario, ResultRecorder recorder) {
            this.scenario = scenario;
            this.recorder = recorder;
        }

        public void execute() {
            try {
                Embedder embedder = new Embedder();
                embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
                embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
                embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), new JBehaveSteps(recorder)).createCandidateSteps());

                List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(JBehavePerformanceTest.class), scenario, "");
                embedder.runStoriesAsPaths(storyPaths);
            } catch (Throwable t) {
                recorder.fail(t.getMessage());
            }
        }
    }
}
