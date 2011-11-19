package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebPerformanceTest extends WebTestCase {

    @Test
    public void demonstrateASimpleWebPerformanceTest() {

        SimpleWebScenario scenario = new SimpleWebScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);                                
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

        assertMinimumThroughput(14, throughputMonitor.getThroughput());
    }

    class SimpleWebScenario extends BaseScenario {

        public void run() {
            begin();
            WebClient webClient = new WebClient();
            try {                
                webClient.setCssEnabled(false);
                webClient.setJavaScriptEnabled(false);                
                
                HtmlPage page = webClient.getPage("http://localhost:8080");
                if (page.getWebResponse().getStatusCode() == 200) {
                    pass();
                } else {                                               
                    fail();
                }
            } catch (Exception e) {
                error();
            } finally {                
                webClient.closeAllWindows();
            }
        }
    }
}
