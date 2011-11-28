package examples;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
import uk.co.acuminous.julez.test.WebTestCase;

public class ConcurrentWebTest extends WebTestCase {

    @Test
    public void demonstrateAConcurrentWebTest() {

        SimpleWebScenario scenario = new SimpleWebScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.register(throughputMonitor);                                
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
        runner.queue(scenarios).allocate(10, THREADS).go();

        assertMinimumThroughput(14, throughputMonitor.getThroughput());
    }

    class SimpleWebScenario extends BaseScenario {

        public void run() {
            raise(eventFactory.begin());
            WebClient webClient = new WebClient();
            try {                
                webClient.setCssEnabled(false);
                webClient.setJavaScriptEnabled(false);                
                
                HtmlPage page = webClient.getPage("http://localhost:8080");
                WebResponse webResponse = page.getWebResponse();
                if (webResponse.getStatusCode() == 200) {
                    raise(eventFactory.pass());
                } else {                                               
                    raiseFailure(webResponse.getStatusCode(), webResponse.getStatusMessage());
                }
            } catch (Exception e) {
                raiseError(e.getMessage());
            } finally {                
                webClient.closeAllWindows();
            }
            raise(eventFactory.end());
        }
        
        private void raiseFailure(Integer status, String message) {
            ScenarioEvent event = eventFactory.fail();
            event.getData().put("statusCode", String.valueOf(status));
            event.getData().put("message", message);
            raise(event);
        }
        
        private void raiseError(String message) {
            ScenarioEvent event = eventFactory.error();
            event.getData().put("message", message);
            raise(event);       
        }
    }
}
