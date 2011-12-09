package uk.co.acuminous.julez.event.pipe;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;

public class PassThroughPipeTest {

    @Test
    public void forwardsEventsToRegisteredHandler() {
        
        TestEventRepository repository = new TestEventRepository();
        
        PassThroughPipe pipe = new PassThroughPipe().register(repository);
        
        Event event = new Event("test");
        pipe.onEvent(event);        
        
        assertSame(event, repository.first());
    }
        
    @Test
    public void tolleratesNoHandlers() {        
        new PassThroughPipe().onEvent(new Event("test"));
    }    
    
}
