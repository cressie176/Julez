package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.InvocationCountingScenario;
import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class SizeLimiterTest {

    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {        
        runner = new ConcurrentScenarioRunner();
    }
    
    @Test
    public void capsNumberOfScenariosToSpecifiedSize() {        
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);
                
        SizeLimiter limiter = new SizeLimiter(scenarios, 10);  
        
        assertEquals(10, limiter.available());                
        
        for (int i = 10; i > 0; i--) {
            assertEquals(i, limiter.available());
            assertNotNull(limiter.next());
        }
                
        assertEquals(0, limiter.available());                
    }
    
    @Test
    public void availabilityNeverFallsBelowZero() {        
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);
                
        SizeLimiter limiter = new SizeLimiter(scenarios, 1);  
        
        assertEquals(1, limiter.available());                
        limiter.next();
        assertEquals(0, limiter.available());
        limiter.next();
        assertEquals(0, limiter.available());
    }     
    
    @Test
    public void tolleratesUnderlyingSourceSmallerThanSpecifiedSize() {        
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 1);
        
        SizeLimiter limiter = new SizeLimiter(scenarios, 10);
        
        assertEquals(1, limiter.available());        
                
        assertNotNull(limiter.next());
        assertEquals(0, limiter.available());        
    } 
    
    @Test
    public void supportsMultiThreading() {        
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 1000);
        
        SizeLimiter limiter = new SizeLimiter(scenarios, 100);  
        
        runner.queue(limiter).allocate(10, THREADS).go();
        
        assertEquals(100, scenario.counter.get());
        assertEquals(0, limiter.available());                
    }    
}
