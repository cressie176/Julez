package uk.co.acuminous.julez.scenario.source;

import uk.co.acuminous.julez.scenario.ScenarioSource;


public abstract class OnDemandScenarioSource implements ScenarioSource {

    @Override
    public int available() {
        return Integer.MAX_VALUE;
    }

}
