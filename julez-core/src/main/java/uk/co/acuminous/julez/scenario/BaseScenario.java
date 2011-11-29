package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.pipe.FanOutPipe;

public abstract class BaseScenario extends FanOutPipe implements Scenario {
    
    protected ScenarioEventFactory eventFactory = new ScenarioEventFactory();   
    
    public void useEventFactory(ScenarioEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
}
