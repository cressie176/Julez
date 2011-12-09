package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class InfiniteScenarioRepeater implements ScenarioSource {

    private final Scenario scenario;
    
    public InfiniteScenarioRepeater(Scenario scenario) {
        this.scenario = scenario;        
    }
    
    @Override
    public Scenario next() {
        return scenario;
    }
}
