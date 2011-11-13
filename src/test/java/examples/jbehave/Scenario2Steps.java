package examples.jbehave;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import uk.co.acuminous.julez.recorder.ResultRecorder;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class Scenario2Steps extends WebSteps {

    private final ResultRecorder resultRecorder;
    
    public Scenario2Steps(ResultRecorder resultsRecorder) {
        this.resultRecorder = resultsRecorder;
    }

    @When("I open the demo page")
    public void openDemoPage() {
        get("http://localhost:8080/");
    }

    @When("search for $query")
    public void searchFor(String query) {
        try {
            currentPage().getElementById("query").setAttribute("value", query);
            click(currentPage().getElementById("search"));
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());                        
            throw new RuntimeException(e);
        }
    }

    @Then("record success")
    public void recordSuccess() {        
        resultRecorder.pass();
    }

    private void get(String url) {
        try {
            setCurrentPage(getWebClient().<Page>getPage(url));
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());            
            throw new RuntimeException(e);            
        }         
        
        failIfNot200();       
    }

    private void click(HtmlElement element) {
        try {
            setCurrentPage(element.click());
        } catch (Exception e) {
            resultRecorder.fail(e.getMessage());
          throw new RuntimeException(e);                        
        }
        
        failIfNot200();
    }    
    
    private void failIfNot200() {
        if (currentPage().getWebResponse().getStatusCode() != 200) {
            resultRecorder.fail(String.valueOf(currentPage().getWebResponse().getStatusCode()));
            throw new RuntimeException("Non 200 Response Code");
        }
    }
}
