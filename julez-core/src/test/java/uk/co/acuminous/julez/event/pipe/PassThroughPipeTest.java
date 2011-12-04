package uk.co.acuminous.julez.event.pipe;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.event.TestEvent;
import uk.co.acuminous.julez.event.handler.EventMonitor;

public class PassThroughPipeTest {

    @Test
    public void forwardsEventsToRegisteredHandler() {
        
        EventMonitor handler = new EventMonitor();
        
        PassThroughPipe pipe = new PassThroughPipe();
        pipe.register(handler);
        
        TestEvent event = new TestEvent();
        pipe.onEvent(event);        
        
        assertSame(event, handler.getEvents().get(0));
    }
    
    
    @Test
    public void tolleratesNoHandlers() {        
        new PassThroughPipe().onEvent(new TestEvent());
    }    
    
}
