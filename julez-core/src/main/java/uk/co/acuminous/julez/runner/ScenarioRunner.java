package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.source.EventSource;

public interface ScenarioRunner extends EventSource {
    public void go();
}
