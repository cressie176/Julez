package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.JulezSugar;

public class ScenarioRepeater implements ScenarioSource {

    private Scenario scenario;
    private Integer repetitions;
    
    public ScenarioRepeater() {        
    }
    
    public ScenarioRepeater(Scenario scenario) {
        this.scenario = scenario;                
    }
    
    public ScenarioRepeater(Scenario scenario, int repetitions) {
        this.scenario = scenario;
        this.repetitions = repetitions;
    }

    public ScenarioRepeater repeat(Scenario scenario) {
        this.scenario = scenario;
        return this;
    }    
    
    public ScenarioRepeater upTo(int repetitions, JulezSugar units) {
        this.repetitions = repetitions;
        return this;
    }    
    
    @Override
    public Scenario next() {
        if (repetitions == null) {
            return scenario;
        }
        return repetitions-- > 0 ? scenario : null;
    }
}
