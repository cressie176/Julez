package examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.acuminous.julez.ConcurrentTestRunner;
import uk.co.acuminous.julez.Scenario;

public class SimplePerformanceTest {

    private static final int MAX_THROUGHPUT = 100;
    private static final int TEST_DURATION = 15;

    @Test
    public void testTheSystemCanSupportTheRequiredNumberOfHelloWorldScenariosPerSecond() {

        ConcurrentTestRunner concurrentTestRunner = new ConcurrentTestRunner(new HelloWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.run();
        int actualThroughput = concurrentTestRunner.actualThroughput();
        
        assertTrue(String.format("Actual throughput: %d scenarios per second", actualThroughput), actualThroughput >= 20);
    }

    class HelloWorldScenario implements Scenario {
        public void execute() {
            System.out.println("Hello World");
        }
    }
}
