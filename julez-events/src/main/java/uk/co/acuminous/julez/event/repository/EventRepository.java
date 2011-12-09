package uk.co.acuminous.julez.event.repository;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.EventPipe;

public interface EventRepository extends EventPipe, Iterable<Event> {
    void raise();    
}
