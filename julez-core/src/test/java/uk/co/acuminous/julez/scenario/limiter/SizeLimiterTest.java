package uk.co.acuminous.julez.scenario.limiter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.TestEventRepository;

public class SizeLimiterTest {

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
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(10, THREADS).initialise();
        new SimpleScenarioRunner().assign(executor).queue(limiter).start();
        
        assertEquals(100, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(100, repository.count(Event.TYPE, ScenarioEvent.END));
    }    
}
