package uk.co.acuminous.julez.event.pipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventPipe;
import uk.co.acuminous.julez.event.EventSource;

public class FanOutPipe implements EventPipe {

    protected final Collection<EventHandler> handlers;

    public FanOutPipe() {
        this(new HashSet<EventHandler>());
    }    
    
    public FanOutPipe(EventHandler... handlers) {
        this.handlers = Arrays.asList(handlers);
    }
    
    public FanOutPipe(Collection<EventHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onEvent(Event event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }

    @Override
    public EventSource register(EventHandler handler) {
        handlers.add(handler);
        return this;
    }
    
    public void registerAll(EventHandler... handlers) {
        for (EventHandler handler : handlers) {
            register(handler);
        }
    }
}