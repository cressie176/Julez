package examples;

import static uk.co.acuminous.julez.PerformanceAssert.assertThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.Scenario;

public class MultiScenarioPerformanceTest {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemSupportsRunningDifferentScenariosInParallel() throws Throwable {

        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
        ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);

        MultiConcurrentScenarioRunner multiTestRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
        multiTestRunner.run();

        assertThroughput(20, runner1.actualThroughput());
        assertThroughput(20, runner2.actualThroughput());
    }

    class HelloWorldScenario implements Scenario {
        public void execute() {
            System.out.println("Hello World");
        }
    }

    class GoodbyeWorldScenario implements Scenario {
        public void execute() {
            System.out.println("Goodbye World");
        }
    }
}
