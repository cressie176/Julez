package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.Event;

import com.google.gson.Gson;

public class ScenarioEvent extends Event<ScenarioEvent> {

    public static final String BEGIN = "ScenarioEvent.BEGIN";
    public static final String PASS = "ScenarioEvent.PASS";
    public static final String FAIL = "ScenarioEvent.FAIL";
    public static final String ERROR = "ScenarioEvent.ERROR";
       
    public ScenarioEvent() {        
    }
    
    public ScenarioEvent(String type, String correlationId) {
        super(type, correlationId);
    }
    
    public ScenarioEvent(String id, long timestamp, String type, String correlationId) {
        super(id, timestamp, type, correlationId);
    }

    @Override
    public ScenarioEvent fromJson(String json) {
        return new Gson().fromJson(json, ScenarioEvent.class);
    }    
}
