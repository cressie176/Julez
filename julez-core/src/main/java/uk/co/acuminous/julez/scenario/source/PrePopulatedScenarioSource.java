package uk.co.acuminous.julez.scenario.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class PrePopulatedScenarioSource implements ScenarioSource {

    private List<Scenario> scenarios;

    public PrePopulatedScenarioSource(Scenario... scenarios) {
        this.scenarios = new ArrayList<Scenario>(Arrays.asList(scenarios));
    }
    
    public PrePopulatedScenarioSource(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    @Override
    public Scenario next() {
        return scenarios.size() > 0 ? scenarios.remove(0) : null;
    }
}
