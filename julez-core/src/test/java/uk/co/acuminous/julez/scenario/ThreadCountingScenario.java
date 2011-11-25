package uk.co.acuminous.julez.scenario;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ThreadCountingScenario extends BaseScenario {

    private Set<Thread> threads = Collections.synchronizedSet(new HashSet<Thread>());
    
    @Override
    public void run() {
    	onEvent(eventFactory.begin());
        threads.add(Thread.currentThread());
        onEvent(eventFactory.pass());        
    }
    
    public int count() {
        return threads.size();
    }

}
