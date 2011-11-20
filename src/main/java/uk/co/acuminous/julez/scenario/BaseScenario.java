package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.BaseEventSource;

public abstract class BaseScenario extends BaseEventSource implements Scenario {
    
    protected ScenarioEventFactory eventFactory = new ScenarioEventFactory();   
    
    public void useEventFactory(ScenarioEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}
