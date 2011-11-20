package uk.co.acuminous.julez.event;

import java.util.UUID;

import uk.co.acuminous.julez.scenario.ScenarioEvent;

import com.google.gson.Gson;

public class Event {
    
    private String id;
    private long timestamp;    
    private String type;
    private String correlationId;        

    public Event(String type, String correlationId) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), type, correlationId);
    }
    
    public Event(String id, long timestamp, String type, String correlationId) {
        this.id = id;
        this.timestamp = timestamp;        
        this.type = type;
        this.correlationId = correlationId;
    }

    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }

    public long getTimestamp() {
        return timestamp;
    }    
    
    public String toString() {
        return toJson();
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }    
    
    public static ScenarioEvent fromJson(String json) {
        return new Gson().fromJson(json, ScenarioEvent.class);
    }
}
