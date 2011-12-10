package uk.co.acuminous.julez.event.filter;

import java.util.ArrayList;
import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;

public abstract class BaseEventFilter extends PassThroughPipe implements EventFilter {

    @Override
    public void onEvent(Event event) {
        if (accept(event)) super.onEvent(event);
    }
    
    @Override
    public BaseEventFilter register(EventHandler handler) {
        super.register(handler);
        return this;
    }
    
    @Override
    public List<Event> applyTo(Iterable<Event> events) {
        List<Event> filteredEvents = new ArrayList<Event>();
        for (Event event: events) {
            if (accept(event)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }        
}
