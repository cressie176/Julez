package uk.co.acuminous.julez.scenario;

import java.util.concurrent.atomic.AtomicInteger;

public class InvocationCountingScenario extends BaseScenario {

    public AtomicInteger counter = new AtomicInteger();        
            
    @Override 
    public void run() {
        handler.onEvent(eventFactory.begin());
        counter.incrementAndGet();
        handler.onEvent(eventFactory.end());
    }
}
