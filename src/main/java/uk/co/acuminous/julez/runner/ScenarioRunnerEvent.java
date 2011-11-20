package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.Event;


public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = "ScenarioRunnerEvent.BEGIN";
    public static String END = "ScenarioRunnerEvent.END"; 
    
    public ScenarioRunnerEvent(String type, String correlationId) {
         super(type, correlationId);
    }
    
    public ScenarioRunnerEvent(String id, long timestamp, String type, String correlationId) {
         super(id, timestamp, type, correlationId);
    }
}
