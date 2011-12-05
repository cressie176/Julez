package uk.co.acuminous.julez.marshalling;

import java.util.HashMap;
import java.util.Map;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.mapper.PatternMapper;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class NamespaceBasedEventClassResolver extends PatternMapper {

    public NamespaceBasedEventClassResolver() {
        super(getMap());
    }
    
    private static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(String.format(Event.EVENT_TYPE_FORMAT, ScenarioEvent.NAMESPACE, ".*"), ScenarioEvent.class.getName());
        map.put(String.format(Event.EVENT_TYPE_FORMAT, ScenarioRunnerEvent.NAMESPACE, ".*"), ScenarioRunnerEvent.class.getName());        
        return map;
    }

}
