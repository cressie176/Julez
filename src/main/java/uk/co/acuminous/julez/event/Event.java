package uk.co.acuminous.julez.event;

import java.util.UUID;

import uk.co.acuminous.julez.scenario.ScenarioEvent;

import com.google.gson.Gson;

public class Event {
    private String id;
    private String type;
    private long timestamp;    
    
    public Event(String type) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), type);
    }
    
    public Event(String id, long timestamp, String type) {
        this.id = id;
        this.timestamp = timestamp;        
        this.type = type;
    }

    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
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
