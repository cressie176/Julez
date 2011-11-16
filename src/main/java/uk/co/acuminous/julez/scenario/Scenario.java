package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.scenario.event.ScenarioEventHandler;

public interface Scenario extends Runnable {

    void run();
    void registerListeners(ScenarioEventHandler... listeners);

}
