package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class ScenarioRepeater implements ScenarioSource {

    private Scenario scenario;
    
    public ScenarioRepeater(Scenario scenario) {
        this.scenario = scenario;                
    }     
    
    @Override
    public Scenario next() {
        return scenario;
    }
}
