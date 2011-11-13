package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import examples.jbehave.Scenario1Steps;

import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.test.WebTestCase;

public class JBehavePerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 50;
    private static final int TEST_DURATION = 15;
    private static final int TEST_TIMEOUT = TEST_DURATION * 2000;

    @Test(timeout=TEST_TIMEOUT)
    public void demonstrateASimpleJBehavePerformanceTest() {

        JBehaveScenario scenario = new JBehaveScenario("scenario1.txt", new Scenario1Steps());
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.useNumberOfWorkers(15);
        concurrentTestRunner.run();

        assertMinimumThroughput(20, concurrentTestRunner.actualThroughput());
    }
}
