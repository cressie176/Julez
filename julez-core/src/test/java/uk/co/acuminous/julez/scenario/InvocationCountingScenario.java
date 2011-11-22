package uk.co.acuminous.julez.scenario;

import java.util.concurrent.atomic.AtomicInteger;

public class InvocationCountingScenario extends BaseScenario {

    public AtomicInteger counter = new AtomicInteger();        
            
    @Override 
    public void run() {
        raise(eventFactory.begin());
        counter.incrementAndGet();
        raise(eventFactory.pass());
    }
}
