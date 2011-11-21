package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.Scenario;

public interface Scenarios {
    Scenario next();
    int available();    
}
