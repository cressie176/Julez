package uk.co.acuminous.julez.event.handler;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;

public class DurationMonitor implements EventHandler {

    private Long started;
    private Long finished;

    @Override
    public void onEvent(Event event) {
        if (ScenarioRunnerEvent.BEGIN.equals(event.getType())) {
            started = event.getTimestamp();
        } else if (ScenarioRunnerEvent.END.equals(event.getType())) {
            finished = event.getTimestamp();
        }
    }

    public long getDuration() {
        if (started == null) {
            return 0;
        } else if (finished == null) {
            return System.currentTimeMillis() - started;
        } else {
            return finished - started;
        }
    }

}
