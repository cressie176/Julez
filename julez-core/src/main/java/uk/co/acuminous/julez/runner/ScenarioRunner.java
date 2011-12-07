package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.EventSource;

public interface ScenarioRunner extends EventSource {

    public enum ConcurrencyUnit {
        THREADS
    };

    public void go();
}
