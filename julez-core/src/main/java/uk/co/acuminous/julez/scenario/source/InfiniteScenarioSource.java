package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;

public class InfiniteScenarioSource implements ScenarioSource {

    private final Scenario scenario;
    
    public InfiniteScenarioSource(Scenario scenario) {
        this.scenario = scenario;        
    }
    
    @Override
    public Scenario next() {
        return scenario;
    }

    @Override
    public int available() {
        return Integer.MAX_VALUE;
    }

}
