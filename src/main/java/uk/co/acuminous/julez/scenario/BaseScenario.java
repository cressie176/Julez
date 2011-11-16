package uk.co.acuminous.julez.scenario;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseScenario implements Scenario {

    protected Set<ScenarioEventHandler> listeners = Collections.synchronizedSet(new HashSet<ScenarioEventHandler>());
    
    @Override
    public void registerListeners(ScenarioEventHandler... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));     
    }
    
    protected void start() {
        notify(new ScenarioEvent(ScenarioEvent.START));
    }    
    
    protected void pass() {
        notify(new ScenarioEvent(ScenarioEvent.PASS));
    }
    
    protected void fail() {
        notify(new ScenarioEvent(ScenarioEvent.FAIL));
    }
    
    protected void notify(ScenarioEvent event) {
        for (ScenarioEventHandler listener : listeners) {
            listener.onScenarioEvent(event);
        }
    }
}
