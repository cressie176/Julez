package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.Event;

public class AsynchronousEventPipe extends PassThroughEventPipe {

    @Override
    public void onEvent(final Event event) {
        new Thread(new Runnable() {
            @Override public void run() {
                handler.onEvent(event);                
            }            
        }).start();
    } 

}
