package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.source.EventSource;

public interface Scenario extends Runnable, EventSource {
    void run();
}
