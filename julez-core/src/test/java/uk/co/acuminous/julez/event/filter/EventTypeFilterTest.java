package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class EventTypeFilterTest {

    private EventMonitor eventMonitor;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();        
    }
    
    @Test
    public void excludesEventsWithWrongBaseType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/PASS");
        filter.register(eventMonitor);
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, eventMonitor.getEvents().size());
    } 
    
    @Test
    public void excludesEventsWithWrongSubType() {
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/END");
        filter.register(eventMonitor);
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(0, eventMonitor.getEvents().size());
    }    

    @Test
    public void includesEventsWithMatchingBaseTypeAndAnySubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/.*");
        filter.register(eventMonitor);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, eventMonitor.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventTypeFilter filter = new EventTypeFilter("ScenarioEvent/BEGIN");
        filter.register(eventMonitor);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, eventMonitor.getEvents().size());
    }
        
    class ChildScenarioEvent extends ScenarioEvent {
        public ChildScenarioEvent() {
            super("id", System.currentTimeMillis(), "type");
        }

    }
}
