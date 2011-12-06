package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventMonitor;

public class EventClassFilterTest {

    private EventMonitor eventMonitor;
    private EventClassFilter<TestEvent> filter;

    @Before
    public void init() {
        eventMonitor = new EventMonitor();        
        filter = new EventClassFilter<TestEvent>(TestEvent.class);                
        filter.register(eventMonitor);        
    }
    
    @Test
    public void excludesEventsWithWrongClass() {                        
        filter.onEvent(new OtherEvent());
        assertEquals(0, eventMonitor.getEvents().size());
    }        

    @Test
    public void includesEventsWithMatchingClass() {        
        filter.onEvent(new TestEvent());
        assertEquals(1, eventMonitor.getEvents().size());
    }    
    
    @Test
    public void includesEventsWithChildClass() {        
        filter.onEvent(new ChildTestEvent());
        assertEquals(1, eventMonitor.getEvents().size());
    }
        
    class TestEvent extends Event {
        public TestEvent() {
            super("test");
        }
    }
    
    class OtherEvent extends Event {
        public OtherEvent() {
            super("other");
        }        
    }
    
    class ChildTestEvent extends TestEvent {}
}
