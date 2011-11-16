package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.event.ThroughputMonitor;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebPerformanceTest extends WebTestCase {

    @Test
    public void demonstrateASimpleWebPerformanceTest() {

        SimpleWebScenario scenario = new SimpleWebScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerListeners(throughputMonitor);                                
        
        new ConcurrentScenarioRunner().queue(scenarios).run();

        assertMinimumThroughput(14, throughputMonitor.getThroughput());
    }

    class SimpleWebScenario extends BaseScenario {

        public void run() {
            try {
                start();
                
                WebClient webClient = new WebClient();
                webClient.setCssEnabled(false);
                webClient.setJavaScriptEnabled(false);                
                webClient.getPage("http://localhost:8080");
                webClient.closeAllWindows();
                
                pass();                
            } catch (Exception e) {
                fail();
            }
        }
    }
}
