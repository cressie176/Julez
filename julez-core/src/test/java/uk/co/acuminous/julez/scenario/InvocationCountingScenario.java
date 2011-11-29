package uk.co.acuminous.julez.scenario;

import java.util.concurrent.atomic.AtomicInteger;

public class InvocationCountingScenario extends BaseScenario {

    public AtomicInteger counter = new AtomicInteger();        
            
    @Override 
    public void run() {
        onEvent(eventFactory.begin());
        counter.incrementAndGet();
        onEvent(eventFactory.end());
    }
}
