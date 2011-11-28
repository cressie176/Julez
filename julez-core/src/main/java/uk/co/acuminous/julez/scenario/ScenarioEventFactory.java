package uk.co.acuminous.julez.scenario;

import uk.co.acuminous.julez.event.Event;


public class ScenarioEventFactory {
    
    private String correlationId;
    
    public ScenarioEventFactory() {
        this(null);
    }
    
    public ScenarioEventFactory(String correlationId) {
        this.correlationId = correlationId;        
    }
    
    public ScenarioEvent begin() {
        return new ScenarioEvent(ScenarioEvent.BEGIN, correlationId);
    }    
    
    public ScenarioEvent pass() {
        return new ScenarioEvent(ScenarioEvent.PASS, correlationId);
    }
    
    public ScenarioEvent fail() {
        return new ScenarioEvent(ScenarioEvent.FAIL, correlationId);
    } 
    
    public ScenarioEvent error() {
        return new ScenarioEvent(ScenarioEvent.ERROR, correlationId);
    }

    public Event end() {
        return new ScenarioEvent(ScenarioEvent.END, correlationId);
    }
}
