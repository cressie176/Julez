package examples;

import static uk.co.acuminous.julez.PerformanceAssert.assertMaxFailures;
import static uk.co.acuminous.julez.PerformanceAssert.assertMinPasses;
import static uk.co.acuminous.julez.PerformanceAssert.assertThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.InMemoryResultRecorder;
import uk.co.acuminous.julez.ResultRecorder;
import uk.co.acuminous.julez.Scenario;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebPerformanceTest extends WebTestCase {

    private static final int MAX_THROUGHPUT = 100;
    private static final int TEST_DURATION = 15;
    
    @Test
    public void testTheSystemCanSupportTheRequiredNumberOfSimpleWebScenariosPerSecond() {

        ResultRecorder recorder = new InMemoryResultRecorder();
        SimpleWebScenario scenario = new SimpleWebScenario(recorder);
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.run();

        assertMinPasses(1, recorder.successCount());
        assertMaxFailures(5, recorder.failureCount());
        assertThroughput(50, concurrentTestRunner.actualThroughput());
    }

    class SimpleWebScenario implements Scenario {

        ResultRecorder recorder;

        SimpleWebScenario(ResultRecorder recorder) {
            this.recorder = recorder;
        }

        public void execute() {

            WebClient webClient = new WebClient();
            webClient.setCssEnabled(false);
            webClient.setJavaScriptEnabled(false);

            try {
                HtmlPage page = webClient.getPage("http://localhost:8080");
                if (page.getWebResponse().getStatusCode() != 200) {
                    recorder.fail(String.valueOf(page.getWebResponse().getStatusCode()));
                } else {
                    recorder.pass();
                }
            } catch (Exception e) {
                recorder.fail(e.getMessage());
            } finally {
                webClient.closeAllWindows();
            }
        }
    }
}
