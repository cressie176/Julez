package uk.co.acuminous.julez.runner;

public interface ScenarioRunner {

    public enum ConcurrencyUnit {
        THREADS
    };

    public void go();
}
