package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class ScenarioRepeater implements ScenarioSource {

    public enum ScenarioRepeaterUnit {
        REPETITIONS
    }
    
    private Scenario scenario;
    
    public ScenarioRepeater(Scenario scenario) {
        this.scenario = scenario;                
    }     
    
    public SizeLimiter repeat(int repetitions) {
        return new SizeLimiter(this, repetitions);
    }
    
    public SizeLimiter limitTo(int repetitions, ScenarioRepeaterUnit units) {
        return repeat(repetitions);
    }
    
    @Override
    public Scenario next() {
        return scenario;
    }
}
