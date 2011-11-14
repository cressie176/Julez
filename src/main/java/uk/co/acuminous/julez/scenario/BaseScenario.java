package uk.co.acuminous.julez.scenario;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseScenario implements Scenario {

    protected Set<ScenarioListener> listeners = Collections.synchronizedSet(new HashSet<ScenarioListener>());
    
    @Override
    public void registerListener(ScenarioListener listener) {
        listeners.add(listener);     
    }
    
    protected void notifyComplete() {
        for (ScenarioListener listener : listeners) {
            listener.onComplete();
        }
    }
}
