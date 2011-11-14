package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebPerformanceTest extends WebTestCase {

    @Test
    public void demonstrateASimpleWebPerformanceTest() {

        Scenarios scenarios = TestUtils.getScenarios(new SimpleWebScenario(), 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.run();

        assertMinimumThroughput(14, runner.throughput());
    }

    class SimpleWebScenario extends BaseScenario {

        public void run() {

            WebClient webClient = new WebClient();
            webClient.setCssEnabled(false);
            webClient.setJavaScriptEnabled(false);

            try {
                webClient.getPage("http://localhost:8080");
            } catch (Exception e) {
                // See ResultRecordingPerformanceTest for how to handle errors
            } finally {
                webClient.closeAllWindows();
                notifyComplete();
            }
        }
    }
}
