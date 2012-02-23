package uk.co.acuminous.julez.scenario.limiter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class DurationLimiter implements ScenarioSource {

    private ScenarioSource scenarios;
    private long timeout;
    private long startTime;

    public DurationLimiter(ScenarioSource scenarios, int value, TimeUnit timeUnit) {
        this.scenarios = scenarios;
        this.timeout = MILLISECONDS.convert(value, timeUnit);
    }

    public DurationLimiter() {
    }
    
    public DurationLimiter limit(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;
    }    

    public DurationLimiter to(int value, TimeUnit timeUnit) {
        this.timeout = MILLISECONDS.convert(value, timeUnit);
        return this;
    }    

    @Override
    public Scenario next() {
        long now = System.currentTimeMillis();
        if (startTime == 0) {
            startTime = now;
        }        
        return now < startTime + timeout ? scenarios.next() : null;
    }
}
