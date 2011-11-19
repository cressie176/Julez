package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.Event;

public class ScenarioEvent extends Event {

    public static final String BEGIN = "ScenarioEvent.BEGIN";
    public static final String PASS = "ScenarioEvent.PASS";
    public static final String FAIL = "ScenarioEvent.FAIL";
    public static final String ERROR = "ScenarioEvent.ERROR";
        
    public ScenarioEvent(String type) {
        super(type);
    }
    
    public ScenarioEvent(String id, long timestamp, String type) {
        super(id, timestamp, type);
    }
    
    public static ScenarioEvent begin() {
        return new ScenarioEvent(BEGIN);
    }    
    
    public static ScenarioEvent pass() {
        return new ScenarioEvent(PASS);
    }
    
    public static ScenarioEvent fail() {
        return new ScenarioEvent(FAIL);
    } 
    
    public static ScenarioEvent error() {
        return new ScenarioEvent(ERROR);
    }    
}
