package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventPipe;
import uk.co.acuminous.julez.event.EventSource;
import uk.co.acuminous.julez.event.handler.NullHandler;
import uk.co.acuminous.julez.event.source.BaseEventSource;

public class PassThroughPipe extends BaseEventSource implements EventPipe {

    protected EventHandler handler = new NullHandler();

    @Override
    public EventSource register(EventHandler handler) {
        this.handler = handler;
        return this;
    }
    
    @Override
    public void onEvent(Event event) {
        handler.onEvent(event);
    }
}
