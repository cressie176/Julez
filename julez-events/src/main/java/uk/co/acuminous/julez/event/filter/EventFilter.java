package uk.co.acuminous.julez.event.filter;

import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.EventPipe;

public interface EventFilter extends EventPipe {
	
	// TODO returning a List here is a bit questionable, and discourages filter chaining,
	// but I'd rather check-in now and address this later.
    List<Event> applyTo(Iterable<Event> events);
    
    boolean accept(Event event);    
}
