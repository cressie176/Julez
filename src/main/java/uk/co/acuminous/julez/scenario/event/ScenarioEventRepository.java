package uk.co.acuminous.julez.scenario.event;


public interface ScenarioEventRepository {

    ScenarioEvent get(String id);
    int count();
    void add(ScenarioEvent event);
    void dump();
    void dump(String type);
}
