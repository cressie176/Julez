package uk.co.acuminous.julez.event.pipe;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventHandler;

public class AsynchronousPipeTest {

    @Test
    public void handlesEventsAsynchronously() throws InterruptedException {
        
        final Thread originalThread = Thread.currentThread();
        final CountDownLatch latch = new CountDownLatch(1);
        
        EventHandler handler = new EventHandler() {
            @Override public void onEvent(Event event) {
                assertFalse(Thread.currentThread() == originalThread); 
                latch.countDown();
            }            
        };
        
        AsynchronousPipe pipe = new AsynchronousPipe();        
        pipe.register(handler);
        
        pipe.onEvent(new Event("test"));
        
        latch.await(5, SECONDS);
        
        assertEquals(0, latch.getCount());
    }
    
}
