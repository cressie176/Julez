package uk.co.acuminous.julez.event.pipe;

import java.util.Collection;
import java.util.HashSet;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventPipe;

public class FanOutPipe implements EventPipe {

    protected final Collection<EventHandler> handlers;

    public FanOutPipe(Collection<EventHandler> handlers) {
        this.handlers = handlers;
    }

    public FanOutPipe() {
        this(new HashSet<EventHandler>());
    }

    @Override
    public void onEvent(Event event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }

    @Override
    public void register(EventHandler handler) {
        handlers.add(handler);
    }
    
    public void registerAll(EventHandler... handlers) {
        for (EventHandler handler : handlers) {
            register(handler);
        }
    }
}
