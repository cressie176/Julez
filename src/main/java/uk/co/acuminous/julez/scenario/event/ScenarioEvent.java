package uk.co.acuminous.julez.scenario.event;

import java.util.UUID;

import com.google.gson.Gson;

public class ScenarioEvent {

    public static final String START = "START";
    public static final String PASS = "PASS";
    public static final String FAIL = "FAIL";
    
    private String id;
    private String type;
    private long timestamp;    
    
    public ScenarioEvent(String type) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), type);
    }
    
    public ScenarioEvent(String id, long timestamp, String type) {
        this.id = id;
        this.timestamp = timestamp;        
        this.type = type;
    }
    
    public static ScenarioEvent start() {
        return new ScenarioEvent(START);
    }    
    
    public static ScenarioEvent pass() {
        return new ScenarioEvent(PASS);
    }
    
    public static ScenarioEvent fail() {
        return new ScenarioEvent(FAIL);
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
