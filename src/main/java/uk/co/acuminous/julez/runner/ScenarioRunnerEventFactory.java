package uk.co.acuminous.julez.runner;



public class ScenarioRunnerEventFactory {

    private final String correlationId;
    
    public ScenarioRunnerEventFactory(String correlationId) {
        this.correlationId = correlationId;        
    }
    
    public ScenarioRunnerEvent begin() {
        return new ScenarioRunnerEvent(ScenarioRunnerEvent.BEGIN, correlationId);
    }

    public ScenarioRunnerEvent end() {
        return new ScenarioRunnerEvent(ScenarioRunnerEvent.END, correlationId);
    }    
    
}
