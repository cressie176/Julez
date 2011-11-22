package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class CappedScenarioRepeater implements ScenarioSource {

    private final ScenarioSource source;    
    
    public CappedScenarioRepeater(Scenario scenario, int repetitions) {
        InfiniteScenarioRepeater repeater = new InfiniteScenarioRepeater(scenario);
        source = new SizeLimiter(repeater, repetitions);
    }   
    
    @Override
    public Scenario next() {
        return source.next();
    }

    @Override
    public int available() {
        return source.available();
    }
}
