package uk.co.acuminous.julez.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class MultiConcurrentScenarioRunnerTest {

    private EventMonitor eventMonitor;
    private ConcurrentScenarioRunner runner1;
    private ConcurrentScenarioRunner runner2;
    private MultiConcurrentScenarioRunner multiRunner;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();
        
        runner1 = new ConcurrentScenarioRunner().queue(new SizedScenarioRepeater(new NoOpScenario(), 0));
        runner2 = new ConcurrentScenarioRunner().queue(new SizedScenarioRepeater(new NoOpScenario(), 0));        
        multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
    }
    
    @Test
    public void startsAllRunners() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, ScenarioRunnerEvent.BEGIN);
        filter.register(eventMonitor);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, eventMonitor.getEvents().size());
    }
        
    @Test
    public void waitsForAllRunnersToFinish() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, ScenarioRunnerEvent.END);
        filter.register(eventMonitor);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, eventMonitor.getEvents().size());
    } 
    
    @Test
    public void raisesBeginAndEndEvents() {
        multiRunner.register(eventMonitor);
        multiRunner.go();
        
        assertEquals(2, eventMonitor.getEvents().size());
        assertEquals(ScenarioRunnerEvent.BEGIN, eventMonitor.getEvents().get(0).getType());
        assertEquals(ScenarioRunnerEvent.END, eventMonitor.getEvents().get(1).getType());
    }    
    
}
