package uk.co.acuminous.julez.runner;

import java.util.Collections;
import java.util.Map;

public class ScenarioRunnerEventFactory {
    
    private final Map<String, String> data;

    public ScenarioRunnerEventFactory() {
        this.data = Collections.emptyMap();
    }
    
    public ScenarioRunnerEventFactory(Map<String, String> data) {
        this.data = data;
    }

    protected ScenarioRunnerEvent newInstance(String type) {
        ScenarioRunnerEvent event = new ScenarioRunnerEvent(type);
        event.getData().putAll(data);
        return event;
    }
    
    public ScenarioRunnerEvent begin() {
        return newInstance(ScenarioRunnerEvent.BEGIN);
    }

    public ScenarioRunnerEvent end() {
        return newInstance(ScenarioRunnerEvent.END);
    }    
    
}
