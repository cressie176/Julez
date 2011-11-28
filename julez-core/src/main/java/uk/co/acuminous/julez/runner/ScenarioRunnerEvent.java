package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.Event;

public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = qualify("BEGIN");
    public static String END = qualify("END"); 
    
    protected ScenarioRunnerEvent() {        
    }
    
    public ScenarioRunnerEvent(String type, String correlationId) {
         super(type, correlationId);
    }
    
    public ScenarioRunnerEvent(String id, Long timestamp, String type, String correlationId) {
         super(id, timestamp, type, correlationId);
    }

    protected static String qualify(String subType) {
        return String.format(EVENT_TYPE_FORMAT, "ScenarioRunnerEvent", subType);
    }    
}
