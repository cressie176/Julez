package uk.co.acuminous.julez.event.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public class EventMonitor implements EventHandler {

    private List<Event> events = Collections.synchronizedList(new ArrayList<Event>());
    
    @Override
    public void onEvent(Event event) {
        events.add(event);            
    }

    public List<Event> getEvents() {
        return events;
    }        
}
