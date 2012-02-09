package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class ScenarioRunnerScenario extends BaseScenario {
    
    private final ScenarioRunner runner;

    public ScenarioRunnerScenario(ScenarioRunner runner) {
        this.runner = runner;            
    }

    @Override
    public void run() {
        runner.start();        
    }
}
