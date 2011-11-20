package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.EventRecorder;

public class EventClassFilterTest {

    private EventRecorder recorder;
    private EventClassFilter<ScenarioEvent> filter;

    @Before
    public void init() {
        recorder = new EventRecorder();        
        filter = new EventClassFilter<ScenarioEvent>(ScenarioEvent.class);                
        filter.registerEventHandler(recorder);        
    }
    
    @Test
    public void excludesEventsWithWrongClass() {                        
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, recorder.events.size());
    }        

    @Test
    public void includesEventsWithMatchingClass() {        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, recorder.events.size());
    }    
    
    @Test
    public void includesEventsWithChildClass() {        
        filter.onEvent(new ChildScenarioEvent());
        assertEquals(1, recorder.events.size());
    }
        
    class ChildScenarioEvent extends ScenarioEvent {
        public ChildScenarioEvent() {
            super("a", System.currentTimeMillis(), "Foo", null);
        }

    }
}
