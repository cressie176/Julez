package uk.co.acuminous.julez.event.repository;

import java.util.HashMap;
import java.util.Map;

import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class InMemoryScenarioEventRepository implements ScenarioEventRepository {

    Map<String, ScenarioEvent> events = new HashMap<String, ScenarioEvent>();
    
    @Override
    public ScenarioEvent get(String id) {
        return events.get(id);
    }

    @Override
    public int count() {
        return events.size();
    }

    @Override
    public void add(ScenarioEvent event) {
        events.put(event.getId(), event);
    }

    @Override
    public void dump() {
        for (ScenarioEvent event : events.values()) {
            System.out.println(event);
        }
    }

    @Override
    public void dump(String type) {
        for (ScenarioEvent event : events.values()) {
            if (event.getType().equals(type)) {
                System.out.println(event);
            }
        }
    }

}
