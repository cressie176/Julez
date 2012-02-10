package uk.co.acuminous.julez.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.executor.SequentialScenarioExecutor;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.control.NoOpScenario;
import uk.co.acuminous.julez.scenario.control.ScenarioRunnerTerminator;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.TestEventRepository;

public class SimpleScenarioRunnerTest {

    @Test
    public void runsWithZeroConfig() {        
        new SimpleScenarioRunner().start();
    }    
    
    @Test
    public void raisesBeginEvent() {
        TestEventRepository testRepository = new TestEventRepository();
                                
        new SimpleScenarioRunner().register(testRepository).start();
                
        assertEquals(testRepository.first().getType(), ScenarioRunnerEvent.BEGIN);        
    } 
        
    @Test
    public void raisesEndEvent() {
        TestEventRepository testRepository = new TestEventRepository();
                                
        new SimpleScenarioRunner().register(testRepository).start();
                
        assertEquals(testRepository.last().getType(), ScenarioRunnerEvent.END);        
    }     
    
    @Test
    public void runsScenarios() {
        TestEventRepository testRepository = new TestEventRepository();
                        
        NoOpScenario scenario = new NoOpScenario();
        scenario.register(new EventDataFilter().filterEventsWhere(Event.TYPE).matches(ScenarioEvent.BEGIN).register(testRepository));
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(3, SCENARIOS);
        new SimpleScenarioRunner().queue(scenarios).assign(new SequentialScenarioExecutor()).start();
                
        assertEquals(3, testRepository.count());        
    }       

    @Test
    public void supportsPlugableExecutors() {
        
        DummyScenarioExecutor executor = new DummyScenarioExecutor();                
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(new NoOpScenario())).to(3, SCENARIOS);
                
        new SimpleScenarioRunner().queue(scenarios).assign(executor).start();
               
        assertEquals(3, executor.invocations);                
    }
    
    @Test
    public void stopsDequeuingScenarios() {

        final SimpleScenarioRunner runner = new SimpleScenarioRunner();
        
        ScenarioSource scenarios = new ScenarioHopper(new ScenarioRunnerTerminator(runner), new NoOpScenario());
        
        runner.queue(scenarios).assign(new SequentialScenarioExecutor());
        runner.start();
               
        assertNotNull("Runner dequeued all scenarios instead of stopping", scenarios.next());                  
    }
    
    public class DummyScenarioExecutor implements ScenarioExecutor {

        public int invocations;
        public boolean stopped;

        @Override
        public void execute(Scenario scenario) {
            scenario.run();
            invocations++;
        }

        @Override
        public void stop() {
            stopped = true;
        }        
    } 
}
