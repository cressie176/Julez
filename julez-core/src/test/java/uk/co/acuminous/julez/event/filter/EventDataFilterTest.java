package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class EventDataFilterTest {

    private EventMonitor eventMonitor;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();        
    }
    
    @Test
    public void excludesEventsWithWrongNamespace() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Scenario/pass");
        filter.register(eventMonitor);
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, eventMonitor.getEvents().size());
    } 
    
    @Test
    public void excludesEventsWithWrongLocalName() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Scenario/end");
        filter.register(eventMonitor);
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(0, eventMonitor.getEvents().size());
    }    

    @Test
    public void includesEventsWithMatchingNamespace() {     
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Scenario/.*");
        filter.register(eventMonitor);        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, eventMonitor.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Scenario/begin");
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
