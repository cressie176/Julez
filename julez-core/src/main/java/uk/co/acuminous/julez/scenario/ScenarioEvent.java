package uk.co.acuminous.julez.scenario;

import java.util.Map;

import uk.co.acuminous.julez.event.Event;

public class ScenarioEvent extends Event {

    public static final String NAMESPACE = "Scenario"; 
    public static final String BEGIN = qualify("begin");
    public static final String PASS = qualify("pass");
    public static final String FAIL = qualify("fail");
    public static final String ERROR = qualify("error");
    public static final String END = qualify("end");
       
    public ScenarioEvent(Map<String, String> eventData) {
        super(eventData);
    }    
    
    public ScenarioEvent(String type) {
        super(type);
    }
    
    public ScenarioEvent(String id, Long timestamp, String type) {
        super(id, timestamp, type);
    }   
    
    protected static String qualify(String localName) {
        return String.format(EVENT_TYPE_FORMAT, NAMESPACE, localName);
    }
}
