package uk.co.acuminous.julez;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

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

    @When("I open BBC news")
    public void openBBCNews() {
        page = get("http://www.bbc.co.uk/news");
    }

    @When("search for $query")
    public void searchFor(String query) {
        try {
            page.getElementById("blq-search").setAttribute("value", query);
            page.getElementById("blq-search-btn").click();
        } catch (Exception e) {
            recorder.fail(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            webClient.closeAllWindows();
        }
    }

    @Then("record success")
    public void recordSuccess() {
        recorder.pass();
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
        } finally {
            webClient.closeAllWindows();
        }
    }
}
