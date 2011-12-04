package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class EventClassFilterTest {

    private EventMonitor eventMonitor;
    private EventClassFilter<ScenarioEvent> filter;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();        
        filter = new EventClassFilter<ScenarioEvent>(ScenarioEvent.class);                
        filter.register(eventMonitor);        
    }
    
    @Test
    public void excludesEventsWithWrongClass() {                        
        filter.onEvent(new ScenarioRunnerEventFactory().begin());
        assertEquals(0, eventMonitor.getEvents().size());
    }        

    @Test
    public void includesEventsWithMatchingClass() {        
        filter.onEvent(new ScenarioEventFactory().begin());
        assertEquals(1, eventMonitor.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithChildClass() {        
        filter.onEvent(new ChildScenarioEvent());
        assertEquals(1, eventMonitor.getEvents().size());
    }
        
    class ChildScenarioEvent extends ScenarioEvent {
        public ChildScenarioEvent() {
            super("a", System.currentTimeMillis(), "Foo");
        }

    }
}
