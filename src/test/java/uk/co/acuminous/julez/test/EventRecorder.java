package uk.co.acuminous.julez.test;

import java.util.ArrayList;
import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;


public class EventRecorder implements EventHandler {

    public List<Event> events = new ArrayList<Event>();
    
    @Override
    public void onEvent(Event event) {
        events.add(event);            
    }        
}
