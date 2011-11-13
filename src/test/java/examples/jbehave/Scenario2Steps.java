package examples.jbehave;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import uk.co.acuminous.julez.recorder.ResultRecorder;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Scenario2Steps extends WebSteps {

    private final ResultRecorder resultRecorder;    
    private final WebClient webClient;
    private HtmlPage page;    

    public Scenario2Steps(ResultRecorder recorder) {
        this.resultRecorder = recorder;
        this.webClient = getWebClient();
    }

    @When("I open the demo page")
    public void openDemoPage() {
        get("http://localhost:8080/");
    }

    @When("search for $query")
    public void searchFor(String query) {
        try {
            page.getElementById("query").setAttribute("value", query);
            click(page.getElementById("search"));
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());                        
            throw new RuntimeException(e);
        }
    }

    @Then("record success")
    public void recordSuccess() {        
        resultRecorder.pass();
        webClient.closeAllWindows();
    }

    private void get(String url) {
        try {
            page = webClient.getPage(url);
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());            
            throw new RuntimeException(e);            
        }         
        
        failIfNot200();       
    }
    
    private void click(HtmlElement element) {
        try {
            page = element.click();
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());
        }
        
        failIfNot200();
    }    
    
    private void failIfNot200() {
        if (page.getWebResponse().getStatusCode() != 200) {
            resultRecorder.fail(String.valueOf(page.getWebResponse().getStatusCode()));
        }
    }
}
