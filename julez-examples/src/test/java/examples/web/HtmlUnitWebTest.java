package examples.web;

import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.WebTestCase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class HtmlUnitWebTest extends WebTestCase {

    @Test
    public void demonstrateAConcurrentWebTestUsingHtmlUnit() {

        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();
        
        Scenario scenario = new HtmlUnitScenario().register(throughputMonitor);
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(100, TIMES);                                                                     
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(throughputMonitor).queue(scenarios).start();

        System.out.println("\nHtmlUnit Throughput\n----------------");
        System.out.println(throughputMonitor.getThroughput());
    }

    class HtmlUnitScenario extends BaseScenario {

        public void run() {
            handler.onEvent(eventFactory.begin());
            WebClient webClient = new WebClient();
            try {                
                webClient.setCssEnabled(false);
                webClient.setJavaScriptEnabled(false);                
                
                HtmlPage page = webClient.getPage("http://localhost:28081");
                WebResponse webResponse = page.getWebResponse();
                if (webResponse.getStatusCode() == 200) {
                    handler.onEvent(eventFactory.pass());
                } else {                                               
                    fail(webResponse.getStatusCode(), webResponse.getStatusMessage());
                }
            } catch (Exception e) {
                error(e.getMessage());
            } finally {                
                webClient.closeAllWindows();
            }
            handler.onEvent(eventFactory.end());
        }
        
        private void fail(Integer status, String message) {
            Event event = eventFactory.fail();
            event.getData().put("statusCode", String.valueOf(status));
            event.getData().put("message", message);
            handler.onEvent(event);
        }
        
        private void error(String message) {
            Event event = eventFactory.error();
            event.getData().put("message", message);
            handler.onEvent(event);       
        }
    }
}
