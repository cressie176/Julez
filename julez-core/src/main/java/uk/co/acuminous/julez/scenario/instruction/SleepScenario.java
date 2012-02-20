package uk.co.acuminous.julez.scenario.instruction;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class SleepScenario extends BaseScenario {

    private long duration;

    public SleepScenario() {
        this(1, SECONDS);
    }
    
    public SleepScenario(long duration, TimeUnit units) {
        sleepFor(duration, units);       
    }    

    public SleepScenario sleepFor(long duration, TimeUnit units) {
        this.duration = MILLISECONDS.convert(duration, units);
        return this;
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
