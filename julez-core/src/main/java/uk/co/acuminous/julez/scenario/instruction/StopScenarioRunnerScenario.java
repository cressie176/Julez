package uk.co.acuminous.julez.scenario.instruction;

import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;

public class StopScenarioRunnerScenario extends BaseScenario {

    private ScenarioRunner runner;

    public StopScenarioRunnerScenario(ScenarioRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());
        runner.stop();
        handler.onEvent(eventFactory.end());        
    }
}
