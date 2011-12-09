package uk.co.acuminous.julez.event.filter;

import java.util.Collection;
import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.EventPipe;

public interface EventFilter extends EventPipe {
    List<Event> applyTo(Collection<Event> events);
}
