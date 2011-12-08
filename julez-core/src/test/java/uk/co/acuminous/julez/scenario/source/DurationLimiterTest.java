package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;



public class DurationLimiterTest {
    
    @Test
    public void limitsQueueToASpecifiedDuration() {
        
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);
        
        DurationLimiter limiter = new DurationLimiter(scenarios, 2, SECONDS);
        
        assertNotNull("Queue was limited too soon", limiter.next());
          
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertNotNull("Queue was limited too soon", limiter.next());
        
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertNull("Queue was not limited aster 2 seconds", limiter.next());
    }
    
    @Test
    public void returnsUndelryingQueueSizeWhileDurationNotReached() {
        
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);
        
        DurationLimiter limiter = new DurationLimiter(scenarios, 1, SECONDS);
        limiter.next();
        
        assertEquals(99, limiter.available());
         
        ConcurrencyUtils.sleep(2, SECONDS);
        
        assertEquals(0, limiter.available());
    }    
}
