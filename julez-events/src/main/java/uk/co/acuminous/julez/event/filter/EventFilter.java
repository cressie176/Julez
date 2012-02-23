package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.EventPipe;

public interface EventFilter extends EventPipe {
    Iterable<Event> applyTo(Iterable<Event> events);
    boolean accept(Event event);    
}
