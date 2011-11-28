package uk.co.acuminous.julez.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Event {
    public static final String EVENT_TYPE_FORMAT = "%s/%s";

    public static final String ID = "~ID";
	public static final String TYPE = "~TYPE";
	public static final String TIMESTAMP = "~TIMESTAMP";
    public static final String CORRELATION_ID = "~CORRELATION_ID";

    protected Map<String, String> data;

    public Event(Map<String, String> data) {
    	this.data = data;
    }
    
    public Event(String id, long timestamp, String type, String correlationId) {
        this.data = new HashMap<String, String>();
        put(ID, id);
        put(TYPE, type);
        put(TIMESTAMP, String.valueOf(timestamp));
        put(CORRELATION_ID, correlationId);
    }
    
    public Event(String type, String correlationId) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), type, correlationId);
    }
    
    // FIXME hack for some gson marshaller
    protected Event() {
    	data = new HashMap<String, String>();
    }
    
    public void put(String key, String value) {
    	data.put(key, value);
    }
    
    public String get(String key) {
    	return data.get(key);
    }
    
    public String getId() {
		return get(ID);
	}

	public long getTimestamp() {
		return Long.parseLong(get(TIMESTAMP));
	}

	public String getType() {
		return get(TYPE);
	}
    
    public String getCorrelationId() {
        return get(CORRELATION_ID);
    }

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	@Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(data);
        return(sb.toString());
    }
	
	@Override public int hashCode() {
		return 24761 ^ data.hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		if (null == obj || !(obj instanceof Event)) return false;
		Event other = (Event)obj;
		return this.data.equals(other.data);
	}
}
