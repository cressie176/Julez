package examples.jbehave;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public abstract class WebSteps {

    private final ThreadLocal<Page> pageHolder = new  ThreadLocal<Page>();

    protected WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssEnabled(false);
        webClient.setJavaScriptEnabled(false);
        return webClient;
    }

    protected HtmlPage currentPage() {
        return (HtmlPage) pageHolder.get();
    }

    protected void setCurrentPage(Page page) {
        pageHolder.set(page);
    }


    
}
