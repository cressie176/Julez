package uk.co.acuminous.julez.scenario.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.acuminous.julez.scenario.Scenario;

public class ReadyMadeScenarioSource implements ScenarioSource {

    private List<Scenario> scenarios;

    public ReadyMadeScenarioSource(Scenario... scenarios) {
        this.scenarios = new ArrayList<Scenario>(Arrays.asList(scenarios));
    }
    
    public ReadyMadeScenarioSource(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    @Override
    public Scenario next() {
        return scenarios.size() > 0 ? scenarios.remove(0) : null;
    }

    @Override
    public int available() {
        return scenarios.size();
    }
}
