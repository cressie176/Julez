package uk.co.acuminous.julez.scenario.event;

import java.util.concurrent.atomic.AtomicInteger;


public class ThroughputMonitor implements ScenarioEventHandler {

    private long started;
    private AtomicInteger completed = new AtomicInteger();
    
    @Override
    public void onScenarioEvent(ScenarioEvent event) {        
        if (ScenarioEvent.START.equals(event.getType())) {
            started = event.getTimestamp();
        } else if (ScenarioEvent.PASS.equals(event.getType()) || ScenarioEvent.FAIL.equals(event.getType())) {
            completed.incrementAndGet();
        }
    }
    
    public int getThroughput() {
        double duration = Math.max(System.currentTimeMillis() - started, 1);        
        return (int) Math.round(completed.get() * 1000 / duration);
    }
}
