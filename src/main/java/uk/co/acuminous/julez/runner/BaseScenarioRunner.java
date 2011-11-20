package uk.co.acuminous.julez.runner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventSource;

public abstract class BaseScenarioRunner implements ScenarioRunner, EventSource {

    protected Set<EventHandler> handlers = new HashSet<EventHandler>();
    
    @Override
    public void registerEventHandler(EventHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));     
    }  
            
    protected void raise(Event<?> event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }
}
