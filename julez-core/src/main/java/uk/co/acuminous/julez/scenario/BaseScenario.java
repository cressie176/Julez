package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.source.BaseEventSource;

public abstract class BaseScenario extends BaseEventSource implements Scenario {
    
    protected ScenarioEventFactory eventFactory = new ScenarioEventFactory();   
    
    public void useEventFactory(ScenarioEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
    
    @Override
    public BaseScenario register(EventHandler handler) {
        super.register(handler);
        return this;
    }
}
