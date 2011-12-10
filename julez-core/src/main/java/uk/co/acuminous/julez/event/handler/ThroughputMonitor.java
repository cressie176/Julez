package uk.co.acuminous.julez.event.handler;

import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class ThroughputMonitor implements EventHandler {

    private long started;
    private long finished;
    private AtomicInteger completed = new AtomicInteger();

    @Override
    public void onEvent(Event event) {
        if (ScenarioRunnerEvent.BEGIN.equals(event.getType())) {
            started = event.getTimestamp();
        } else if (ScenarioRunnerEvent.END.equals(event.getType())) {
            finished = event.getTimestamp();
        } else if (ScenarioEvent.END.equals(event.getType()) && isInTimeframe(event)) {
            completed.incrementAndGet();
        }
    }

    private boolean isInTimeframe(Event event) {
        return (started != 0 && event.getTimestamp() >= started) && (finished == 0 || event.getTimestamp() <= finished);
    }
    
    public int getThroughput() {
        long effectiveEnd = finished != 0 ? finished : System.currentTimeMillis();
        double duration = Math.max(effectiveEnd - started, 1);
        return (int) Math.round(completed.get() * 1000 / duration);
    }
}
