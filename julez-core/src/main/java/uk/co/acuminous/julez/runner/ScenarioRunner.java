package uk.co.acuminous.julez.runner;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.event.source.EventSource;

public interface ScenarioRunner extends EventSource {
    public void start();
    public void stop(long timeout, TimeUnit units);
}
