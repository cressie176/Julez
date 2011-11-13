package examples;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.util.List;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.SilentEmbedderMonitor;
import org.jbehave.core.embedder.Embedder.RunningStoriesFailed;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.Test;

import examples.jbehave.Scenario1Steps;

import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.WebTestCase;

public class JBehavePerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemSupportsTheRequiredNumberOfJBehaveScenariosPerSecond() {

        JBehaveScenario scenario = new JBehaveScenario("scenario1.txt");
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.useNumberOfWorkers(15);
        concurrentTestRunner.run();

        assertMinimumThroughput(20, concurrentTestRunner.actualThroughput());
    }

    class JBehaveScenario implements Scenario {

        private final String scenario;

        public JBehaveScenario(String scenario) {
            this.scenario = scenario;
        }

        public void execute() {
            Embedder embedder = new Embedder();
            embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
            embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
            embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), new Scenario1Steps()).createCandidateSteps());

            List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(JBehavePerformanceTest.class), scenario, "");
            
            try {
                embedder.runStoriesAsPaths(storyPaths);
            } catch (RunningStoriesFailed e) {
                // Test probably finished leaving some stories queued
            }            
        }
    }
}
