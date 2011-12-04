package uk.co.acuminous.julez.scenario;

import java.util.Collections;
import java.util.Map;

import uk.co.acuminous.julez.event.Event;


public class ScenarioEventFactory {
    
    private Map<String, String> data;
    
    public ScenarioEventFactory() {
        data = Collections.emptyMap();
    }
    
    public ScenarioEventFactory(Map<String, String> data) {
        this.data = data;
    }

    protected ScenarioEvent newInstance(String type) {
       ScenarioEvent event = new ScenarioEvent(type);
       event.getData().putAll(data);
       return event;
    }
    
    public ScenarioEvent begin() {
        return newInstance(ScenarioEvent.BEGIN);
    }    
    
    public ScenarioEvent pass() {
        return newInstance(ScenarioEvent.PASS);
    }
    
    public ScenarioEvent fail() {
        return newInstance(ScenarioEvent.FAIL);
    } 
    
    public ScenarioEvent error() {
        return newInstance(ScenarioEvent.ERROR);
    }

    public Event end() {
        return newInstance(ScenarioEvent.END);
    }
}
