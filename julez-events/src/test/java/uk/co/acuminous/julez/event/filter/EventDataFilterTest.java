package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.DummyEvent;
import uk.co.acuminous.julez.event.handler.EventMonitor;

public class EventDataFilterTest {

    private EventMonitor eventMonitor;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();        
    }
    
    @Test
    public void excludesEventsWithWrongNamespace() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Car/start");
        filter.register(eventMonitor);
        filter.onEvent(new DummyEvent("Race/start"));
        assertEquals(0, eventMonitor.getEvents().size());
    } 
    
    @Test
    public void excludesEventsWithWrongLocalName() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Car/start");
        filter.register(eventMonitor);
        filter.onEvent(new DummyEvent("Car/stop"));
        assertEquals(0, eventMonitor.getEvents().size());
    }    

    @Test
    public void includesEventsWithMatchingNamespace() {     
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Car/.*");
        filter.register(eventMonitor);        
        filter.onEvent(new DummyEvent("Car/skid"));
        assertEquals(1, eventMonitor.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "Car/start");
        filter.register(eventMonitor);        
        filter.onEvent(new DummyEvent("Car/start"));
        assertEquals(1, eventMonitor.getEvents().size());
    }
}
