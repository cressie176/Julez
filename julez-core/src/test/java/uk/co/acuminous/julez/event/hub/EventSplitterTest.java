package uk.co.acuminous.julez.event.hub;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventPipe;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.event.pipe.BaseEventPipe;

public class EventSplitterTest {

    EventMonitor mon1;
    EventMonitor mon2;
	EventSplitter splitter;
	
    @Before
    public void init() {
    	mon1 = new EventMonitor();
    	mon2 = new EventMonitor();

    	splitter = new TestSplitter();
    }
    
    @Test
    public void hubWithNoOutputsPassesNoEvents() {
    	splitter.onEvent(new Event("1", System.currentTimeMillis(), "A", "C1"));
        splitter.onEvent(new Event("2", System.currentTimeMillis(), "B", "C1"));

        assertEquals(0, mon1.getEvents().size());
        assertEquals(0, mon2.getEvents().size());
    }        
    
    @Test
    public void hubWithOneOutputPassesOneEvent() {
    	splitter.register(mon2, "HIGH");

        splitter.onEvent(new Event("1", System.currentTimeMillis(), "A", "C1"));
        splitter.onEvent(new Event("2", System.currentTimeMillis(), "B", "C1"));
        
        assertEquals(0, mon1.getEvents().size());
        assertEquals(1, mon2.getEvents().size());
    }        
    
    @Test
    public void hubWithTwoOutputsPassesTwoEvents() {
    	splitter.register(mon1, "LOW");
    	splitter.register(mon2, "HIGH");

        splitter.onEvent(new Event("1", System.currentTimeMillis(), "A", "C1"));
        splitter.onEvent(new Event("2", System.currentTimeMillis(), "B", "C1"));
        
        assertEquals(1, mon1.getEvents().size());
        assertEquals(1, mon2.getEvents().size());
    }        
    
    @Test
    public void hubWithConvertingOutputConverts() {
    	EventPipe echo = new BaseEventPipe() {
			@Override public void onEvent(Event event) {
				handler.onEvent(event);
				handler.onEvent(new Event(event.getId() + "+", event.getTimestamp()+100, event.getType(), event.getCorrelationId()));
			}
		};
    	splitter.register(mon1, "LOW");
		splitter.register(echo, "HIGH");
    	echo.register(mon2);

        splitter.onEvent(new Event("1", System.currentTimeMillis(), "A", "C1"));
        splitter.onEvent(new Event("2", System.currentTimeMillis(), "B", "C1"));
        
        assertEquals(1, mon1.getEvents().size());
        assertEquals(2, mon2.getEvents().size());
    }        
        
    private final class TestSplitter extends EventSplitter {
		@Override public void onEvent(Event event) {
			if ("A".equals(event.getType())) {
				send(event, "LOW");
			} else {
				send(event, "HIGH");
			}
		}
	}
}
