package uk.co.acuminous.julez.event.source;

import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventSource;
import uk.co.acuminous.julez.event.handler.NullHandler;

public class BaseEventSource implements EventSource {

    protected EventHandler handler = new NullHandler();
    
    @Override
    public EventSource register(EventHandler handler) {
        this.handler = handler;
        return this;
    }    
    
}
