package uk.co.acuminous.julez.test;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class PassFailErrorScenario extends BaseScenario {
    
    private AtomicInteger counter = new AtomicInteger();
    
    @Override public void run() {
        handler.onEvent(eventFactory.begin());
        
        int invocation = counter.incrementAndGet();
        
        if (invocation % 4 == 0) {
            handler.onEvent(eventFactory.fail());
        } else if (invocation % 7 == 0) {
            handler.onEvent(eventFactory.error());
        } else {
            handler.onEvent(eventFactory.pass());                
        }
        
        handler.onEvent(eventFactory.end());
    }        
}
