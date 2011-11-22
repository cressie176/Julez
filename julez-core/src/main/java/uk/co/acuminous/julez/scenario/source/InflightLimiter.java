package uk.co.acuminous.julez.scenario.source;

import java.util.Arrays;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class InflightLimiter implements ScenarioSource, EventHandler {

    private final ScenarioSource scenarios;    
    private final int limit;
    private AtomicInteger counter = new AtomicInteger();

    public InflightLimiter(ScenarioSource scenarios, int limit) {
        this.scenarios = scenarios;
        this.limit = limit;
    }

    @Override
    public Scenario next() {
        while (counter.get() >= limit) {
            ConcurrencyUtils.sleep(100, MILLISECONDS);
        }
        counter.incrementAndGet();
        return scenarios.next();
    }

    @Override
    public int available() {
        return scenarios.available();
    }

    @Override
    public void onEvent(Event event) {
        if (Arrays.asList(ScenarioEvent.PASS, ScenarioEvent.FAIL, ScenarioEvent.ERROR).contains(event.getType())) {
            counter.decrementAndGet();
        }        
    }

}
