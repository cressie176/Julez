package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.EventRecorder;

public class EventTypeFilterTest {

    private EventRecorder recorder;

    @Before
    public void init() {
        recorder = new EventRecorder();        
    }
    
    @Test
    public void excludesEventsWithWrongBaseType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/PASS");
        filter.registerEventHandler(recorder);
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, recorder.events.size());
    } 
    
    @Test
    public void excludesEventsWithWrongSubType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/END");
        filter.registerEventHandler(recorder);
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(0, recorder.events.size());
    }    

    @Test
    public void includesEventsWithMatchingBaseTypeAndAnySubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/.*");
        filter.registerEventHandler(recorder);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, recorder.events.size());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/BEGIN");
        filter.registerEventHandler(recorder);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, recorder.events.size());
    }
        
    class ChildScenarioEvent extends ScenarioEvent {
        public ChildScenarioEvent() {
            super("a", System.currentTimeMillis(), "Foo", null);
        }

    }
}
