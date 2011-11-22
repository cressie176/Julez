package uk.co.acuminous.julez.util;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.source.ScenarioQueue;
import uk.co.acuminous.julez.scenario.source.ScenarioSource;

public class ScenarioRepeater {

    public static ScenarioSource getScenarios(Scenario scenario, int size) {
        ScenarioQueue scenarios = new ScenarioQueue();
        for (int i = 0; i < size; i++) {
            scenarios.add(scenario);
        }
        return scenarios;
    }
}
