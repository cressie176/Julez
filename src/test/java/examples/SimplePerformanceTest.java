package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;

public class SimplePerformanceTest {

    private static final int MAX_THROUGHPUT = 100;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemCanSupportTheRequiredNumberOfHelloWorldScenariosPerSecond() {

        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.run();
        
        assertMinimumThroughput(20, concurrentTestRunner.actualThroughput());
    }

    class HelloWorldScenario implements Scenario {
        public void execute() {
            System.out.println("Hello World");
        }
    }
}
