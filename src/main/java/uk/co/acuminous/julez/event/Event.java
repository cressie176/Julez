package uk.co.acuminous.julez.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

public abstract class Event {
    
    public static final String EVENT_TYPE_FORMAT = "%s/%s";    
    
    private String id;
    private long timestamp;    
    private String type;
    private String correlationId;
    private Map<String, Object> data = new HashMap<String, Object>();

    public Event() {        
    }
    
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
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public String toString() {
        return toJson();
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }    
    
    public Event fromJson(String json) {
        return new Gson().fromJson(json, this.getClass());        
    }
}
