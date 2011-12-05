package uk.co.acuminous.julez.runner;

import java.util.Map;

import uk.co.acuminous.julez.event.Event;

public class ScenarioRunnerEvent extends Event {

    public static String NAMESPACE = "ScenarioRunner";
    public static String BEGIN = qualify("begin");
    public static String END = qualify("end");

    protected ScenarioRunnerEvent() {
    }
    
    public ScenarioRunnerEvent(Map<String, String> eventData) {
        super(eventData);
    }

    public ScenarioRunnerEvent(String type) {
        super(type);
    }

    public ScenarioRunnerEvent(String id, Long timestamp, String type) {
        super(id, timestamp, type);
    }

    protected static String qualify(String localName) {
        return String.format(EVENT_TYPE_FORMAT, NAMESPACE, localName);
    }
}
