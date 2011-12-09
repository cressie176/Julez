package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class PassThroughScenarioSource implements ScenarioSource {

    private final ScenarioSource source;

    public PassThroughScenarioSource(ScenarioSource source) {
        this.source = source;        
    }
    
    @Override
    public Scenario next() {
        return source.next();
    }

}
