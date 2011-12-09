package uk.co.acuminous.julez.scenario;

public interface ScenarioSource {
    
    public enum ScenarioUnit {
        SCENARIOS
    }
    
    Scenario next();
}
