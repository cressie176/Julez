package uk.co.acuminous.julez.scenario;

public interface Scenario extends Runnable {

    void run();
    void registerListeners(ScenarioEventHandler... listeners);

}
