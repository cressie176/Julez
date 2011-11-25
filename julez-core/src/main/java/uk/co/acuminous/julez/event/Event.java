package uk.co.acuminous.julez.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

public abstract class Event {
    public static final String EVENT_TYPE_FORMAT = "%s/%s";    
    
    // TODO I find it puzzling that you provide a no-arg constructor and yet 
    // have no setters for these fields. If they are OK being null, why not just put 
    // them in the data map?
    private String id;
    private long timestamp;    
    private String type;
    private String correlationId;
    
    // TODO I'm intrigued by your choice of String as the value type for this Map
    // are you really expecting anything put in here to be clumsily serialised to 
    // String and back just to be passed along an in-memory handler chain? 
    private Map<String, String> data;

    // NOTE I have added a constructor which allows choice of a different Map implementation
    // Feel free to discuss, but for me this is a no-brainer.
    public Event(Map<String, String> data) {
    	setData(data);
    }
    
    public Event() {
    	this(new HashMap<String, String>());
    }
    
    // TODO as this is such a low-level class I would not bother with this constructor
    // By all means have a subclass (UUIDEvent or whatever) which provides it as a convenience
    // but by placing it here you are implying that all uses of Event will use a UUID for id
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
    
    // TODO _please_ use a more sensible implementation. 
    // IMHO?, toString is mainly for developer examination.
    // Attempting to live with the constraints of an external representation usually causes 
    // more problems than it solves.
    public String toString() {
        return toJson();
    }
    
    // TODO converting to and from any specific representation does not belong in here.
    // And worse, they even drag in dependencies and _force_ a specific implementation.
    // This Event class should be the most flexible and least dependent bit of the whole system.  
    // What were you thinking !
    public String toJson() {
        return new Gson().toJson(this);
    }    
    public Event fromJson(String json) {
        return new Gson().fromJson(json, this.getClass());        
    }
}
