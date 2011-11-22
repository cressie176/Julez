package examples.jbehave;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class Scenario2Steps extends WebSteps {

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
            throw new RuntimeException(e);
        }
    }

    @Then("record success")
    public void recordSuccess() {
        // Do nothing
    }

    private void get(String url) {
        try {
            setCurrentPage(getWebClient().<Page>getPage(url));
        } catch (Exception e) {
            throw new RuntimeException(e);            
        }         
        
        failIfNot200();       
    }

    private void click(HtmlElement element) {
        try {
            setCurrentPage(element.click());
        } catch (Exception e) {
            throw new RuntimeException(e);                        
        }
        
        failIfNot200();
    }    
    
    private void failIfNot200() {
        if (currentPage().getWebResponse().getStatusCode() != 200) {
            throw new RuntimeException("Non 200 Response Code");
        }
    }
}
