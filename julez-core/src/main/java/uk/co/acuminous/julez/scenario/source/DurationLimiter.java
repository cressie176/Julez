package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

// TODO Make fluent
public class DurationLimiter implements ScenarioSource {

    private final ScenarioSource scenarios;
    private final long duration;
    private long endTime;

    public DurationLimiter(ScenarioSource scenarios, long duration, TimeUnit units) {
        this.scenarios = scenarios;
        this.duration = MILLISECONDS.convert(duration, units);
    }

    @Override
    public Scenario next() { 
        long now = System.currentTimeMillis();
        Scenario scenario = null;
        if (endTime == 0) {
            endTime = now + duration;
        };
        if (endTime > now) {
            scenario = scenarios.next();
        }
        return scenario;
    }
}
