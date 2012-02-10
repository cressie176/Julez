package uk.co.acuminous.julez.executor;

import uk.co.acuminous.julez.scenario.Scenario;

public interface ScenarioExecutor {

    void execute(Scenario scenario);
    void stop();

}
