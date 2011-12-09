package uk.co.acuminous.julez.event.pipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;
import uk.co.acuminous.julez.test.TestUtils;

public class FanOutPipeTest {

    @Test
    public void forwardsEventsToAllHandlers() {
        TestEventRepository repository1 = new TestEventRepository();
        TestEventRepository repository2 = new TestEventRepository();
        
        FanOutPipe pipe = new FanOutPipe().registerAll(repository1, repository2);
        
        Event event = new Event("test");
        pipe.onEvent(event);
        
        assertEquals(1, TestUtils.countEvents(repository1));
        assertSame(event, repository1.first());
        
        assertEquals(1, TestUtils.countEvents(repository2));        
        assertSame(event, repository2.first());
    }
    
    @Test
    public void tolleratesNoHandlers() {        
        new FanOutPipe().onEvent(new Event("test"));
    }
    
}
