package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.Event;

public class ScenarioEvent extends Event {

    public static final String BEGIN = qualify("BEGIN");
    public static final String PASS = qualify("PASS");
    public static final String FAIL = qualify("FAIL");
    public static final String ERROR = qualify("ERROR");
       
    public ScenarioEvent() {        
    }
    
    public ScenarioEvent(String type, String correlationId) {
        super(type, correlationId);
    }
    
    public ScenarioEvent(String id, Long timestamp, String type, String correlationId) {
        super(id, timestamp, type, correlationId);
    }   
    
    protected static String qualify(String subType) {
        return String.format(EVENT_TYPE_FORMAT, ScenarioEvent.class.getSimpleName(), subType);
    }
}
