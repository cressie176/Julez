package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;

public class ScenarioRepeater extends BaseScenarioSource {

    private Scenario scenario;
    
    public ScenarioRepeater(Scenario scenario) {
        this.scenario = scenario;                
    }     
    
    @Override
    public Scenario next() {
        return scenario;
    }
}
