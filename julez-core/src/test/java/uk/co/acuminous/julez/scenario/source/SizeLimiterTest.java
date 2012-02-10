package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.control.NoOpScenario;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.test.TestEventRepository;

public class SizeLimiterTest {

    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {        
        runner = new ConcurrentScenarioRunner();
    }
    
    @Test
    public void capsNumberOfScenariosToSpecifiedSize() {     
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(new NoOpScenario())).to(100, SCENARIOS);
                
        SizeLimiter limiter = new SizeLimiter(scenarios, 10);  
                
        for (int i = 10; i > 0; i--) {
            assertNotNull(limiter.next());
        }
                
        assertNull(limiter.next());                
    }   
    
    @Test
    public void tolleratesUnderlyingSourceSmallerThanSpecifiedSize() {      
        
        ScenarioSource scenarios = new ScenarioHopper(new NoOpScenario());        
        
        SizeLimiter limiter = new SizeLimiter(scenarios, 10);
        
        assertNotNull(limiter.next());
        assertNull(limiter.next());        
    } 
    
    @Test
    public void supportsMultiThreading() {
        
        TestEventRepository repository = new TestEventRepository();
        Scenario scenario = new NoOpScenario().register(repository);
                
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(200, SCENARIOS);
        
        SizeLimiter limiter = new SizeLimiter(scenarios, 100);  
        
        runner.queue(limiter).allocate(10, THREADS).start();
        
        assertEquals(100, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(100, repository.count(Event.TYPE, ScenarioEvent.END));
    }    
}
