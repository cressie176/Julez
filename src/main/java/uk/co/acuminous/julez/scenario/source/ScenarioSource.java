package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;

public interface ScenarioSource {
    Scenario next();
    int available();    
}
