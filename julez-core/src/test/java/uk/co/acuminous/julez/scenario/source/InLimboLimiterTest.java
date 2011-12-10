package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.pipe.PassThroughPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.InLimboLimiter;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;


public class InLimboLimiterTest {

    @Test
    public void providesScenariosWhileBelowOrAtSpecifiedLimit() { 
        
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        
        InLimboLimiter limiter = new InLimboLimiter().block(scenarios).when(2, SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED);        
        
        long startTime = System.currentTimeMillis();
        
        assertNotNull(limiter.next());
        assertNotNull(limiter.next());
        assertNotNull(limiter.next());
        
        Long duration = System.currentTimeMillis() - startTime;
        
        assertTrue(duration < 1000);
    }      
    
    @Test
    public void blocksScenariosWhileAboveSpecifiedLimit() { 
        
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        
        final InLimboLimiter limiter = new InLimboLimiter().block(scenarios).when(2, SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED);
        
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
    public void preventsOutOfMemoryErrors() {
                
        final PassThroughPipe passThroughPipe = new PassThroughPipe();
        
        ScenarioSource scenarios = new ScenarioSource() {
            @Override public Scenario next() {                
                SleepingScenario scenario = new SleepingScenario(5, SECONDS) {
                    @SuppressWarnings("unused")
                    String lotsOfData = String.format("%10000d", System.currentTimeMillis());
                };
                scenario.register(passThroughPipe);                
                return scenario;
            }
        };
        
        InLimboLimiter limiter = new InLimboLimiter().block(scenarios).when(100, SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED);
        passThroughPipe.register(limiter);
                
        new ConcurrentScenarioRunner().queue(limiter).allocate(4, THREADS).runFor(5, SECONDS).go();            
    }
    
}
