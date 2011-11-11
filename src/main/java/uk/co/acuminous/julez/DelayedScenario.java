package uk.co.acuminous.julez;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

public class DelayedScenario implements Scenario, Delayed {

    private final DateTime trigger;
    private final Scenario scenario;

    public DelayedScenario(DateTime trigger, Scenario scenario) {
        this.trigger = trigger;
        this.scenario = scenario;
    }

    public void execute() {
        try {
            scenario.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public int compareTo(Delayed other) {
        return trigger.compareTo(((DelayedScenario) other).trigger);
    }

    public long getDelay(TimeUnit timeUnit) {
        Long millis = trigger.getMillis() - new DateTime().getMillis();
        return timeUnit.convert(millis, MILLISECONDS);
    }
}
