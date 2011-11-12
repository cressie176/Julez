package examples;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import uk.co.acuminous.julez.ResultRecorder;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class JBehaveSteps {

    private ResultRecorder recorder;
    private WebClient webClient;
    private HtmlPage page;

    public JBehaveSteps(ResultRecorder recorder) {
        this.recorder = recorder;
        this.webClient = new WebClient();

        webClient.setCssEnabled(false);
        webClient.setJavaScriptEnabled(false);
    }

    @When("I open the demo page")
    public void openDemoPage() {
        page = get("http://localhost:8080/");
    }

    @When("search for $query")
    public void searchFor(String query) {
        try {
            page.getElementById("query").setAttribute("value", query);
            page.getElementById("search").click();
        } catch (Exception e) {
            recorder.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Then("record success")
    public void recordSuccess() {
        recorder.pass();
        webClient.closeAllWindows();
    }

    private HtmlPage get(String url) {
        try {
            HtmlPage page = webClient.getPage(url);
            if (page.getWebResponse().getStatusCode() != 200) {
                recorder.fail(String.valueOf(page.getWebResponse().getStatusCode()));
            }
            return page;
        } catch (Exception e) {
            recorder.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
