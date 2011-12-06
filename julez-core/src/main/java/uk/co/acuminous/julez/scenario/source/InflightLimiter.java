package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class InflightLimiter implements ScenarioSource, EventHandler {

    private final ScenarioSource scenarios;    
    private final int upperLimit;
    private final int lowerLimit;
    
    private AtomicInteger counter = new AtomicInteger();
    private long pause = 100;

    public InflightLimiter(ScenarioSource scenarios, int limit) {
        this(scenarios, limit, limit);
    }
    
    public InflightLimiter(ScenarioSource scenarios, int upperLimit, int lowerLimit) {
        this.scenarios = scenarios;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }    

    @Override
    public Scenario next() {
        if (counter.get() > upperLimit) {
            while (counter.get() > lowerLimit) {
                ConcurrencyUtils.sleep(pause, MILLISECONDS);
            }            
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
        if (ScenarioEvent.END.equals(event.getType())) {
            counter.decrementAndGet();
        }        
    }
        
    public void setPause(long value, TimeUnit units) {
        pause = MILLISECONDS.convert(value, units);
    }    

}
