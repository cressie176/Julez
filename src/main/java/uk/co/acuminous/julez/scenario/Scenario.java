package uk.co.acuminous.julez.scenario;

public interface Scenario extends Runnable {

    void run();
    void registerListener(ScenarioListener listener);

}
