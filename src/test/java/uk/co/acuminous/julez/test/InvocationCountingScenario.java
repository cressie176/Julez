package uk.co.acuminous.julez.test;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.scenario.BaseScenario;


public class InvocationCountingScenario extends BaseScenario {

    public AtomicInteger counter = new AtomicInteger();        
            
    @Override 
    public void run() {
        raise(eventFactory.begin());
        counter.incrementAndGet();
        raise(eventFactory.pass());
    }
}
