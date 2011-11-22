package uk.co.acuminous.julez.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.filter.EventTypeFilter;
import uk.co.acuminous.julez.event.handler.EventRecorder;

public class MultiConcurrentScenarioRunnerTest {

    private EventRecorder recorder;
    private ConcurrentScenarioRunner runner1;
    private ConcurrentScenarioRunner runner2;
    private MultiConcurrentScenarioRunner multiRunner;

    @Before
    public void init() {
        recorder = new EventRecorder();
        
        runner1 = new ConcurrentScenarioRunner();
        runner2 = new ConcurrentScenarioRunner();        
        multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
    }
    
    @Test
    public void startsAllRunners() {
        EventTypeFilter filter = new EventTypeFilter(ScenarioRunnerEvent.BEGIN);
        filter.registerEventHandler(recorder);
        
        runner1.registerEventHandler(filter);
        runner2.registerEventHandler(filter);
        
        multiRunner.run();
        
        assertEquals(2, recorder.events.size());
    }
        
    @Test
    public void waitsForAllRunnersToFinish() {
        EventTypeFilter filter = new EventTypeFilter(ScenarioRunnerEvent.END);
        filter.registerEventHandler(recorder);
        
        runner1.registerEventHandler(filter);
        runner2.registerEventHandler(filter);
        
        multiRunner.run();
        
        assertEquals(2, recorder.events.size());
    } 
    
    @Test
    public void raisesBeginAndEndEvents() {
        multiRunner.registerEventHandler(recorder);
        multiRunner.run();
        
        assertEquals(2, recorder.events.size());
        assertEquals(ScenarioRunnerEvent.BEGIN, recorder.events.get(0).getType());
        assertEquals(ScenarioRunnerEvent.END, recorder.events.get(1).getType());
    }    
    
}