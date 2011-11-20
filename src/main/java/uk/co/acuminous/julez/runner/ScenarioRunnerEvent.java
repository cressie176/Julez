package uk.co.acuminous.julez.runner;

import com.google.gson.Gson;

import uk.co.acuminous.julez.event.Event;

public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = "ScenarioRunnerEvent/BEGIN";
    public static String END = "ScenarioRunnerEvent/END"; 
    
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
}
