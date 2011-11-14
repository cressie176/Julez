package uk.co.acuminous.julez.scenario;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

public class DelayedScenario extends BaseScenario implements Delayed {

    private final DateTime trigger;
    private final Scenario scenario;

    public DelayedScenario(DateTime trigger, Scenario scenario) {
        this.trigger = trigger;
        this.scenario = scenario;
    }

    public void run() {
        scenario.run();
    }

    public int compareTo(Delayed other) {
        return trigger.compareTo(((DelayedScenario) other).trigger);
    }

    public long getDelay(TimeUnit timeUnit) {
        Long millis = trigger.getMillis() - System.currentTimeMillis();
        return timeUnit.convert(millis, MILLISECONDS);
    }
}
