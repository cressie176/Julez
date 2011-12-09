package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class SizeLimiter implements ScenarioSource {

    private final ScenarioSource scenarios;
    private long count;

    public SizeLimiter(ScenarioSource scenarios, long sizeLimit) {
        this.scenarios = scenarios;        
        this.count = sizeLimit;
    }

    @Override
    public Scenario next() {
        Scenario scenario = null;
        if (count > 0) {
            count--;
            return scenarios.next();
        }
        return scenario;
    }
}
