package uk.co.acuminous.julez.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Event {
    
    public static final String EVENT_TYPE_FORMAT = "%s/%s";    
    
    private String id;
    private long timestamp;    
    private String type;
    private String correlationId;
    private Map<String, String> data = new HashMap<String, String>();

    protected Event() {        
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
    
    public Map<String, String> getData() {
        return data;
    }
    
    public void setData(Map<String, String> data) {
        this.data = data;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",").append(type).append(",").append(timestamp);
        return(sb.toString());
    }
}
