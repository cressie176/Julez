package uk.co.acuminous.julez.event.pipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;

public class FanOutEventPipeTest {

    @Test
    public void forwardsEventsToAllHandlers() {
        TestEventRepository repository1 = new TestEventRepository();
        TestEventRepository repository2 = new TestEventRepository();
        
        FanOutEventPipe pipe = new FanOutEventPipe().registerAll(repository1, repository2);
        
        Event event = new Event("test");
        pipe.onEvent(event);
        
        assertEquals(1, repository1.count());
        assertSame(event, repository1.first());
        
        assertEquals(1, repository2.count());        
        assertSame(event, repository2.first());
    }
    
    @Test
    public void tolleratesNoHandlers() {        
        new FanOutEventPipe().onEvent(new Event("test"));
    }
    
}
