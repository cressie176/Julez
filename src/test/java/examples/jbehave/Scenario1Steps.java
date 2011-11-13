package examples.jbehave;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scenario1Steps extends WebSteps {

    private final WebClient webClient;
    private HtmlPage page;

    public Scenario1Steps() {
        this.webClient = getWebClient();
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
            throw new RuntimeException(e);
        }
    }

    @Then("record success")
    public void recordSuccess() {        
        webClient.closeAllWindows();
    }

    private HtmlPage get(String url) {
        try {
            return webClient.getPage(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
