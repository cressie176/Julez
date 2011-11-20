package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.BaseEventSource;

public abstract class BaseScenario extends BaseEventSource implements Scenario {

    protected final ScenarioEventFactory eventFactory;
    
    public BaseScenario(ScenarioEventFactory eventFactory) {
        this.eventFactory = eventFactory;        
    }
}
