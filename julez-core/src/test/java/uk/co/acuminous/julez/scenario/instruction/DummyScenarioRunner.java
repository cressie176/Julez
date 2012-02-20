package uk.co.acuminous.julez.scenario.instruction;

import uk.co.acuminous.julez.runner.BaseScenarioRunner;

public class DummyScenarioRunner extends BaseScenarioRunner {

    boolean started;
    boolean stopped; 
    
    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        stopped = true;
    }

}
