package uk.co.acuminous.julez.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseEventSource implements EventSource {

    protected final Set<EventHandler> handlers = new HashSet<EventHandler>();

    @Override
    public void registerEventHandler(EventHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));     
    }    
    
    protected void raise(Event event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }
}
