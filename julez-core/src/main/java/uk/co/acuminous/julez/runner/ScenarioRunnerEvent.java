package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.Event;

public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = qualify("BEGIN");
    public static String END = qualify("END"); 
    
    protected ScenarioRunnerEvent() {        
    }
    
    public ScenarioRunnerEvent(String type) {
         super(type);
    }
    
    public ScenarioRunnerEvent(String id, Long timestamp, String type) {
         super(id, timestamp, type);
    }

    protected static String qualify(String subType) {
        return String.format(EVENT_TYPE_FORMAT, "ScenarioRunnerEvent", subType);
    }    
}
