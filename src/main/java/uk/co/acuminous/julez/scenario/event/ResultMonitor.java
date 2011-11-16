package uk.co.acuminous.julez.scenario.event;

import java.util.concurrent.atomic.AtomicInteger;


public class ResultMonitor implements ScenarioEventHandler {

    private AtomicInteger passCount = new AtomicInteger();
    private AtomicInteger failureCount = new AtomicInteger();
    
    @Override
    public void onScenarioEvent(ScenarioEvent event) {
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
