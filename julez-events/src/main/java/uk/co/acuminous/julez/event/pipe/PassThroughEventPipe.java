package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.source.BaseEventSource;

public class PassThroughEventPipe extends BaseEventSource implements EventPipe {

    @Override
    public PassThroughEventPipe register(EventHandler handler) {        
        super.register(handler);
        return this;
    }
    
    @Override
    public void onEvent(Event event) {
        handler.onEvent(event);
    }
}
