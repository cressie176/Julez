package uk.co.acuminous.julez.marshalling.json;

import java.lang.reflect.Type;
import java.util.Map;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.mapper.OneWayMapper;
import uk.co.acuminous.julez.marshalling.EventTranslator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonEventTranslator implements EventTranslator {

    private final OneWayMapper eventClassResolver;

    public JsonEventTranslator(OneWayMapper eventClassResolver) {
        this.eventClassResolver = eventClassResolver;        
    }
    
    @Override
    public String marshall(Event event) {
        return new Gson().toJson(event.getData());
    }    
    
    @Override
    public Event unmarshall(String json) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();                        
        Map<String, String> data = new Gson().fromJson(json, type);        
        String className = eventClassResolver.getValue(data.get(Event.TYPE));
        try {
            return (Event) Class.forName(className).getConstructor(Map.class).newInstance(data);            
        } catch (Exception e) {
            throw new RuntimeException("Error unmarshalling json: " + json, e);
        }
    }
}
