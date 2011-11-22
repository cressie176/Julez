package uk.co.acuminous.julez.scenario;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.util.ConcurrencyUtils;

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
        raise(eventFactory.begin());        
        ConcurrencyUtils.sleep(duration, MILLISECONDS);
        raise(eventFactory.pass());        
    }
}