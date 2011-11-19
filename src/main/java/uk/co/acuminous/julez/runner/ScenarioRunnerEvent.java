package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.Event;


public class ScenarioRunnerEvent extends Event {

    public static String BEGIN = "ScenarioRunnerEvent.BEGIN";
    public static String END = "ScenarioRunnerEvent.END"; 
    
    public ScenarioRunnerEvent(String type) {
         super(type);
    }
    
    public ScenarioRunnerEvent(String id, long timestamp, String type) {
         super(id, timestamp, type);
    }

    public static Event begin() {
        return new ScenarioRunnerEvent(BEGIN);
    }

    public static Event end() {
        return new ScenarioRunnerEvent(END);
    }
}
