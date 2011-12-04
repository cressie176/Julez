package uk.co.acuminous.julez.event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public abstract class Event {
    public static final String EVENT_TYPE_FORMAT = "%s/%s";

    public static final String ID = "#ID";
    public static final String TYPE = "#TYPE";
    public static final String TIMESTAMP = "#TIMESTAMP";

    protected Map<String, String> data;

    // FIXME hack for some gson marshaller
    protected Event() {
        data = new HashMap<String, String>();
    }    
    
    public Event(Map<String, String> data) {
        this.data = data;
    }

    public Event(String id, long timestamp, String type) {
        this.data = new HashMap<String, String>();
        put(ID, id);
        put(TYPE, type);
        put(TIMESTAMP, String.valueOf(timestamp));
    }

    public Event(String type) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), type);
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

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();     
        Set<String> keys = new TreeSet<String>(data.keySet());
        Map<String, String> sortedData = new LinkedHashMap<String, String>();
        for (String key : keys) {
            sortedData.put(key, data.get(key));
        }        
        sb.append(this.getClass().getName()).append(sortedData);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return 24761 ^ data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (null == obj || !(obj instanceof Event)) return false;
        Event other = (Event) obj;
        return this.data.equals(other.data);
    }
}
