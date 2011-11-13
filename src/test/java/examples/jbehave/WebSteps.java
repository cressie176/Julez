package examples.jbehave;

import com.gargoylesoftware.htmlunit.WebClient;

public abstract class WebSteps {

    protected WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssEnabled(false);
        webClient.setJavaScriptEnabled(false);
        return webClient;
    }


    
}
