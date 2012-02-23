package uk.co.acuminous.julez.scenario.instruction;

import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;

public class ScenarioRunnerStarter extends BaseScenario {
    
    private final ScenarioRunner runner;

    public ScenarioRunnerStarter(ScenarioRunner runner) {
        this.runner = runner;            
    }

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());        
        runner.start();
        handler.onEvent(eventFactory.end());
    }
}
