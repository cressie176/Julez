package uk.co.acuminous.julez.marshalling.json;

import java.lang.reflect.Type;
import java.util.Map;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.marshalling.EventTranslator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonEventTranslator implements EventTranslator {
    
    @Override
    public String marshall(Event event) {
        return new Gson().toJson(event.getData());
    }    
    
    @Override
    public Event unmarshall(String json) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();                        
        Map<String, String> data = new Gson().fromJson(json, type);
        return new Event(data);
    }
}
