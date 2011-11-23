package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.EventRelay;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.SleepingScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class InflightLimiterTest {

    @Test
    public void returnsScenariosWhileBelowOrAtSpecifiedLimit() { 
        
        ScenarioSource source = new InfiniteScenarioRepeater(new NoOpScenario());
        
        InflightLimiter limiter = new InflightLimiter(source, 3);
        
        long startTime = System.currentTimeMillis();
        assertNotNull(limiter.next());
        assertEquals(Integer.MAX_VALUE, limiter.available());

        assertNotNull(limiter.next());
        assertEquals(Integer.MAX_VALUE, limiter.available());

        assertNotNull(limiter.next());
        assertEquals(Integer.MAX_VALUE, limiter.available());
        Long duration = System.currentTimeMillis() - startTime;
        
        assertTrue(duration < 1000);
    }
    
    @Test
    public void blocksScenariosWhileAboveSpecifiedLimit() { 
        
        ScenarioSource source = new InfiniteScenarioRepeater(new NoOpScenario());
        
        final InflightLimiter limiter = new InflightLimiter(source, 3);
        
        limiter.next();
        limiter.next();
        limiter.next();        

        Thread t = new Thread(new Runnable() {

            @Override public void run() {
                ConcurrencyUtils.sleep(1, SECONDS);
                limiter.onEvent(new ScenarioEventFactory().pass());
            }
            
        });
        t.start();
        
        long startTime = System.currentTimeMillis();
        assertNotNull(limiter.next());
        long duration = System.currentTimeMillis() - startTime;
        
        assertTrue("Inflight Limiter did not block after limit was exceeded", duration >= 1000);        
    }    
    
    @Test
    public void inflightLimiterPreventsOutOfMemoryErrors() {
                
        final EventRelay relay = new EventRelay();
        
        ScenarioSource scenarios = new OnDemandScenarioSource() {
            @Override public Scenario next() {
                SleepingScenario scenario = new SleepingScenario();
                scenario.register(relay);
                return scenario;
            }
        };
        
        InflightLimiter limiter = new InflightLimiter(scenarios, 100);
        relay.register(limiter);
                
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        
        runner.queue(limiter).allocate(10, THREADS).runFor(60, SECONDS).go();            

    }
    
}
