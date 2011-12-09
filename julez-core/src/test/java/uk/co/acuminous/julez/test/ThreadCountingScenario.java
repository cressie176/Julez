package uk.co.acuminous.julez.test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class ThreadCountingScenario extends BaseScenario {

    private Set<Thread> threads = Collections.synchronizedSet(new HashSet<Thread>());
    
    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());
        threads.add(Thread.currentThread());
        handler.onEvent(eventFactory.end());        
    }
    
    public int count() {
        return threads.size();
    }

}
