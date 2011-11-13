package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.WebTestCase;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebPerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 100;
    private static final int TEST_DURATION = 15;
    private static final int TEST_TIMEOUT = TEST_DURATION * 2000;

    @Test(timeout=TEST_TIMEOUT)
    public void demonstrateASimpleWebPerformanceTest() {

        SimpleWebScenario scenario = new SimpleWebScenario();
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.run();

        assertMinimumThroughput(14, concurrentTestRunner.actualThroughput());
    }

    class SimpleWebScenario implements Scenario {

        public void execute() {

            WebClient webClient = new WebClient();
            webClient.setCssEnabled(false);
            webClient.setJavaScriptEnabled(false);

            try {
                webClient.getPage("http://localhost:8080");
            } catch (Exception e) {
                // See recorder example for how to handle errors
            } finally {
                webClient.closeAllWindows();
            }
        }
    }
}
