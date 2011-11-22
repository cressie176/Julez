package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.Event;

import com.google.gson.Gson;

public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = qualify("BEGIN");
    public static String END = qualify("END"); 
    
    public ScenarioRunnerEvent() {        
    }
    
    public ScenarioRunnerEvent(String type, String correlationId) {
         super(type, correlationId);
    }
    
    public ScenarioRunnerEvent(String id, long timestamp, String type, String correlationId) {
         super(id, timestamp, type, correlationId);
    }

    @Override
    public ScenarioRunnerEvent fromJson(String json) {
        return new Gson().fromJson(json, ScenarioRunnerEvent.class);
    }
    
    protected static String qualify(String subType) {
        return String.format(EVENT_TYPE_FORMAT, ScenarioRunnerEvent.class.getSimpleName(), subType);
    }    
}
