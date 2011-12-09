package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.pipe.PassThroughPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class InflightLimiterTest {

    @Test
    public void returnsScenariosWhileBelowOrAtSpecifiedLimit() { 
        
        ScenarioSource source = new ScenarioRepeater(new NoOpScenario());
        
        InflightLimiter limiter = new InflightLimiter(source, 2);
        
        long startTime = System.currentTimeMillis();
        
        assertNotNull(limiter.next());
        assertNotNull(limiter.next());
        assertNotNull(limiter.next());
        
        Long duration = System.currentTimeMillis() - startTime;
        
        assertTrue(duration < 1000);
    }
    
    @Test
    public void blocksScenariosWhileAboveSpecifiedLimit() { 
        
        ScenarioSource source = new ScenarioRepeater(new NoOpScenario());
        
        final InflightLimiter limiter = new InflightLimiter(source, 2);
        
        limiter.next();
        limiter.next();
        limiter.next();        

        ConcurrencyUtils.start(new Runnable() {
            @Override public void run() {
                ConcurrencyUtils.sleep(1, SECONDS);
                limiter.onEvent(new ScenarioEventFactory().end());
            }            
        });
        
        long startTime = System.currentTimeMillis();
        assertNotNull(limiter.next());
        long duration = System.currentTimeMillis() - startTime;
        
        assertTrue("Inflight Limiter did not block after limit was exceeded", duration >= 1000);        
    }    
    
    @Test
    public void inflightLimiterPreventsOutOfMemoryErrors() {
                
        final PassThroughPipe passThroughPipe = new PassThroughPipe();
        
        ScenarioSource scenarios = new ScenarioSource() {
            @Override public Scenario next() {
                SleepingScenario scenario = new SleepingScenario();
                scenario.register(passThroughPipe);
                return scenario;
            }
        };
        
        InflightLimiter limiter = new InflightLimiter(scenarios, 100);
        passThroughPipe.register(limiter);
                
        new ConcurrentScenarioRunner().queue(limiter).allocate(10, THREADS).runFor(60, SECONDS).go();            

    }
    
}
