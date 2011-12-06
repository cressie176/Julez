package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousPipe extends BaseEventPipe {

    @Override
    public void onEvent(final Event event) {
        ConcurrencyUtils.start(new Runnable() {
            @Override public void run() {
                handler.onEvent(event);                
            }            
        });
    } 

}
