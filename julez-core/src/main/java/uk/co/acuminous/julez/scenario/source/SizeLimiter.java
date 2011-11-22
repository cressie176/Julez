package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class SizeLimiter implements ScenarioSource {

    private final ScenarioSource scenarios;
    private int count;

    public SizeLimiter(ScenarioSource scenarios, int sizeLimit) {
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

    @Override
    public int available() {
        int limitedAvailability = Math.max(0, count);
        int underlyingAvailability = scenarios.available();
        return Math.min(limitedAvailability, underlyingAvailability);
    }

}
