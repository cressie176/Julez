package uk.co.acuminous.julez.event.handler;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class ResultMonitor implements EventHandler {

    private AtomicInteger passCount = new AtomicInteger();
    private AtomicInteger failureCount = new AtomicInteger();
    private AtomicInteger errorCount = new AtomicInteger();

    @Override
    public void onEvent(Event event) {
        if (ScenarioEvent.PASS.equals(event.getType())) {
            passCount.incrementAndGet();
        } else if (ScenarioEvent.FAIL.equals(event.getType())) {
            failureCount.incrementAndGet();
        } else if (ScenarioEvent.ERROR.equals(event.getType())) {
            errorCount.incrementAndGet();
        }
    }

    public int getPassCount() {
        return passCount.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public int getErrorCount() {
        return errorCount.get();
    }

    public int getPercentage() {
        int successful = getPassCount();
        int unsuccessful = getFailureCount() + getErrorCount();
        int total = successful + unsuccessful;
        return total > 0 ? (successful * 100) / total : 0;
    }

}
