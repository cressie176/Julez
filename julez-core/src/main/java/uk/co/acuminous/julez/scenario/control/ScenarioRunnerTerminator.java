package uk.co.acuminous.julez.scenario.control;

import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;

public class ScenarioRunnerTerminator extends BaseScenario {

    private ScenarioRunner runner;

    public ScenarioRunnerTerminator(ScenarioRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());
        runner.stop();
        handler.onEvent(eventFactory.end());        
    }
}
