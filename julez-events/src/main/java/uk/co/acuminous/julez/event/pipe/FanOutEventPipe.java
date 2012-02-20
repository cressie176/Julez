package uk.co.acuminous.julez.event.pipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventHandler;

public class FanOutEventPipe implements EventPipe {

    protected final Collection<EventHandler> handlers;

    public FanOutEventPipe() {
        this(new HashSet<EventHandler>());
    }    
    
    public FanOutEventPipe(EventHandler... handlers) {
        this.handlers = Arrays.asList(handlers);
    }
    
    public FanOutEventPipe(Collection<EventHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onEvent(Event event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }

    @Override
    public FanOutEventPipe register(EventHandler handler) {
        handlers.add(handler);
        return this;
    }
    
    public FanOutEventPipe registerAll(EventHandler... handlers) {
        for (EventHandler handler : handlers) {
            register(handler);
        }
        return this;
    }
}
