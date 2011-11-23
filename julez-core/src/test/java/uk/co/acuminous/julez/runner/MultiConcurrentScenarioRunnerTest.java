package uk.co.acuminous.julez.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.filter.EventTypeFilter;
import uk.co.acuminous.julez.event.handler.EventRecorder;
import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class MultiConcurrentScenarioRunnerTest {

    private EventRecorder recorder;
    private ConcurrentScenarioRunner runner1;
    private ConcurrentScenarioRunner runner2;
    private MultiConcurrentScenarioRunner multiRunner;

    @Before
    public void init() {
        recorder = new EventRecorder();
        
        runner1 = new ConcurrentScenarioRunner().queue(new SizedScenarioRepeater(new NoOpScenario(), 0));
        runner2 = new ConcurrentScenarioRunner().queue(new SizedScenarioRepeater(new NoOpScenario(), 0));        
        multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
    }
    
    @Test
    public void startsAllRunners() {
        EventTypeFilter filter = new EventTypeFilter(ScenarioRunnerEvent.BEGIN);
        filter.register(recorder);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, recorder.getEvents().size());
    }
        
    @Test
    public void waitsForAllRunnersToFinish() {
        EventTypeFilter filter = new EventTypeFilter(ScenarioRunnerEvent.END);
        filter.register(recorder);
        
        runner1.register(filter);
        runner2.register(filter);
        
        multiRunner.go();
        
        assertEquals(2, recorder.getEvents().size());
    } 
    
    @Test
    public void raisesBeginAndEndEvents() {
        multiRunner.register(recorder);
        multiRunner.go();
        
        assertEquals(2, recorder.getEvents().size());
        assertEquals(ScenarioRunnerEvent.BEGIN, recorder.getEvents().get(0).getType());
        assertEquals(ScenarioRunnerEvent.END, recorder.getEvents().get(1).getType());
    }    
    
}
