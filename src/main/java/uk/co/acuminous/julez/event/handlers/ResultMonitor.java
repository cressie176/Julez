package uk.co.acuminous.julez.event.handlers;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.ScenarioEvent;


public class ResultMonitor implements EventHandler {

    private AtomicInteger passCount = new AtomicInteger();
    private AtomicInteger failureCount = new AtomicInteger();
    
    @Override
    public void onEvent(Event event) {
        if (ScenarioEvent.PASS.equals(event.getType())) {
            passCount.incrementAndGet();
        } else if (ScenarioEvent.FAIL.equals(event.getType())) {
            failureCount.incrementAndGet();
        }
    }
    
    public int getPassCount() {
        return passCount.get();
    }
    
    public int getFailureCount() {
        return failureCount.get();
    }
    
    public int getPercentage() {
        int passes = getPassCount();
        int failures = getFailureCount();
        int totalCount = passes + failures;
        return totalCount > 0 ? (passes * 100) / totalCount : 0;
    }

}
