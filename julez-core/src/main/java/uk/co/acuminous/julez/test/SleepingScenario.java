package uk.co.acuminous.julez.test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

// TODO Make fluent
public class SleepingScenario extends BaseScenario {

    private final long duration;

    public SleepingScenario() {
        this(1, SECONDS);
    }
    
    public SleepingScenario(long duration, TimeUnit units) {
        this.duration = MILLISECONDS.convert(duration, units);
        
    }    

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());        
        if (ConcurrencyUtils.sleep(duration, MILLISECONDS)) {
            handler.onEvent(eventFactory.pass());
        } else {
            handler.onEvent(eventFactory.error());
        }
        handler.onEvent(eventFactory.end());        
    }
}
