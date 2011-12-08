package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class DeferredScenarioSource implements ScenarioSource {

    private final ScenarioSource scenarios;    
    private final long startTime;
    
    public DeferredScenarioSource(ScenarioSource scenarios, long startTime) {
        this.scenarios = scenarios;
        this.startTime = startTime;

    }

    @Override
    public Scenario next() {
        ConcurrencyUtils.sleepUntil(startTime);
        return scenarios.next();
    }

    @Override
    public int available() {
        return scenarios.available();
    }

}
