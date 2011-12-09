package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.ScenarioSource;

public abstract class BaseScenarioSource implements ScenarioSource {

    public SizeLimiter limitRepetitionsTo(long repetitions) {
        return new SizeLimiter(this, repetitions);
    }
}
