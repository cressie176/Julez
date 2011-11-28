package uk.co.acuminous.julez.event.marshaller;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventMarshaller;
import uk.co.acuminous.julez.event.EventUnmarshaller;

import com.google.gson.Gson;

public class JsonEventMarshaller implements EventMarshaller, EventUnmarshaller{

    @Override
    public String marshal(Event event) {
        return new Gson().toJson(event);
    }    
    
    @Override
    public Event unmarshall(String json) {
        return new Gson().fromJson(json, Event.class);
    }
}
