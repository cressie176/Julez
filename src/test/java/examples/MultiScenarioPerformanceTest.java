package examples;

import static uk.co.acuminous.julez.PerformanceAssert.assertThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.ConcurrentTestRunner;
import uk.co.acuminous.julez.MultiConcurrentTestRunner;
import uk.co.acuminous.julez.Scenario;

public class MultiScenarioPerformanceTest {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemSupportsRunningDifferentScenariosInParallel() throws Throwable {

        ConcurrentTestRunner runner1 = new ConcurrentTestRunner(new HelloWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
        ConcurrentTestRunner runner2 = new ConcurrentTestRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);

        MultiConcurrentTestRunner multiTestRunner = new MultiConcurrentTestRunner(runner1, runner2);
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
