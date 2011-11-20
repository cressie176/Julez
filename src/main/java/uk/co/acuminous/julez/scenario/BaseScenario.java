package uk.co.acuminous.julez.scenario;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public abstract class BaseScenario implements Scenario {

    protected final Set<EventHandler> handlers = new HashSet<EventHandler>();
    protected final ScenarioEventFactory eventFactory;
    
    public BaseScenario(ScenarioEventFactory eventFactory) {
        this.eventFactory = eventFactory;        
    }
    
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
