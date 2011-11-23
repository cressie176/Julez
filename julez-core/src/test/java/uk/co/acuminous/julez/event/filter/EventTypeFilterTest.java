package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.EventRecorder;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class EventTypeFilterTest {

    private EventRecorder recorder;

    @Before
    public void init() {
        recorder = new EventRecorder();        
    }
    
    @Test
    public void excludesEventsWithWrongBaseType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/PASS");
        filter.register(recorder);
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, recorder.getEvents().size());
    } 
    
    @Test
    public void excludesEventsWithWrongSubType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/END");
        filter.register(recorder);
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(0, recorder.getEvents().size());
    }    

    @Test
    public void includesEventsWithMatchingBaseTypeAndAnySubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/.*");
        filter.register(recorder);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, recorder.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/BEGIN");
        filter.register(recorder);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, recorder.getEvents().size());
    }
        
    class ChildScenarioEvent extends ScenarioEvent {
        public ChildScenarioEvent() {
            super("a", System.currentTimeMillis(), "Foo", null);
        }

    }
}
