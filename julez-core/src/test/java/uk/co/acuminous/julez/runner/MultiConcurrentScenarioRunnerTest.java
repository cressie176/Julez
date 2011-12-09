package uk.co.acuminous.julez.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.filter.EventFilter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.test.TestEventRepository;


public class MultiConcurrentScenarioRunnerTest {

    private TestEventRepository repository;
    private ConcurrentScenarioRunner runner1;
    private ConcurrentScenarioRunner runner2;
    private MultiConcurrentScenarioRunner multiRunner;

    @Before
    public void init() {
        repository = new TestEventRepository();        
        runner1 = new ConcurrentScenarioRunner().queue(new ScenarioHopper());
        runner2 = new ConcurrentScenarioRunner().queue(new ScenarioHopper());        
        multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
    }
    
    @Test
    public void startsAllRunners() {
        EventFilter filter = new EventDataFilter(Event.TYPE, ScenarioRunnerEvent.BEGIN).register(repository);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, repository.count());
    }
        
    @Test
    public void waitsForAllRunnersToFinish() {
        EventFilter filter = new EventDataFilter(Event.TYPE, ScenarioRunnerEvent.END).register(repository);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, repository.count());
    } 
    
    @Test
    public void raisesBeginEvent() {
        multiRunner.register(repository).go();        
        assertEquals(ScenarioRunnerEvent.BEGIN, repository.first().getType());
    }    
    
    @Test
    public void raisesEndEvent() {
        multiRunner.register(repository).go();        
        assertEquals(ScenarioRunnerEvent.END, repository.last().getType());        
    }
    
}
