package uk.co.acuminous.julez.scenario.limiter;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class SizeLimiter implements ScenarioSource {

    private ScenarioSource scenarios;
    private long limit;

    public SizeLimiter() {        
    }    
    
    public SizeLimiter(ScenarioSource scenarios, long limit) {
        this.scenarios = scenarios;        
        this.limit = limit;
    }
    
    public SizeLimiter restrict(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;
    }
    
    public SizeLimiter applyAt(long limit) {
        this.limit = limit;
        return this;
    }
    
    @Override
    public Scenario next() {
        Scenario scenario = null;
        if (limit > 0) {
            limit--;
            return scenarios.next();
        }
        return scenario;
    }
}
