package uk.co.acuminous.julez.event.repository;

import uk.co.acuminous.julez.scenario.ScenarioEvent;

public interface ScenarioEventRepository {
    ScenarioEvent get(String id);
    int count();
    void add(ScenarioEvent event);
    void dump();
    void dump(String type);
}
