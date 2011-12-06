package uk.co.acuminous.julez.test;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class PassFailErrorScenario extends BaseScenario {
    
    private AtomicInteger counter = new AtomicInteger();
    
    @Override public void run() {
        onEvent(eventFactory.begin());
        
        int invocation = counter.incrementAndGet();
        
        if (invocation % 4 == 0) {
            onEvent(eventFactory.fail());
        } else if (invocation % 7 == 0) {
            onEvent(eventFactory.error());
        } else {
            onEvent(eventFactory.pass());                
        }
        
        onEvent(eventFactory.end());
    }        
}
