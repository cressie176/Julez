package uk.co.acuminous.julez.scenario.source;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.ScenarioSource;

public abstract class BaseScenarioSource implements ScenarioSource {

    public SizeLimiter limitRepetitionsTo(int repetitions) {
        return new SizeLimiter(this, repetitions);
    }
    
    public DurationLimiter limitDurationTo(long duration, TimeUnit units) {
        return new DurationLimiter(this, duration, units);
    }
    
}
