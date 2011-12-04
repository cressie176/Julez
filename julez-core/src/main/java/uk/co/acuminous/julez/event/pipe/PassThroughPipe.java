package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.Event;

public class PassThroughPipe extends BaseEventPipe {

    @Override
    public void onEvent(Event event) {
        if (null != handler) handler.onEvent(event);
    }        
}
