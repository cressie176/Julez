package examples;

import static uk.co.acuminous.julez.PerformanceAssert.assertThroughput;

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
        
        assertThroughput(20, concurrentTestRunner.actualThroughput());
    }

    class HelloWorldScenario implements Scenario {
        public void execute() {
            System.out.println("Hello World");
        }
    }
}
