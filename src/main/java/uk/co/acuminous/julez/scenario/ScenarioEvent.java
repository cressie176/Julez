package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.Event;

public class ScenarioEvent extends Event {

    public static final String BEGIN = "ScenarioEvent.BEGIN";
    public static final String PASS = "ScenarioEvent.PASS";
    public static final String FAIL = "ScenarioEvent.FAIL";
    public static final String ERROR = "ScenarioEvent.ERROR";
        
    public ScenarioEvent(String type, String correlationId) {
        super(type, correlationId);
    }
    
    public ScenarioEvent(String id, long timestamp, String type, String correlationId) {
        super(id, timestamp, type, correlationId);
    }    
}
