package uk.co.acuminous.julez.executor;

import uk.co.acuminous.julez.scenario.Scenario;

public class SynchronousScenarioExecutor implements ScenarioExecutor {

    @Override
    public void execute(Scenario scenario) {
        scenario.run();        
    }

    @Override
    public void stop() {        
    }

}
