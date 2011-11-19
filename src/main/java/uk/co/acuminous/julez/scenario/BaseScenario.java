package uk.co.acuminous.julez.scenario;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public abstract class BaseScenario implements Scenario {

    protected Set<EventHandler> handlers = new HashSet<EventHandler>();
    
    @Override
    public void registerEventHandler(EventHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));     
    }
    
    protected void begin() {
        raise(new ScenarioEvent(ScenarioEvent.BEGIN));
    }    
    
    protected void pass() {
        raise(new ScenarioEvent(ScenarioEvent.PASS));
    }
    
    protected void fail() {
        raise(new ScenarioEvent(ScenarioEvent.FAIL));
    }
        
    protected void error() {
        raise(new ScenarioEvent(ScenarioEvent.ERROR));
    }    
    
    protected void raise(Event event) {
        for (EventHandler handler : handlers) {
            handler.onEvent(event);
        }
    }
}
