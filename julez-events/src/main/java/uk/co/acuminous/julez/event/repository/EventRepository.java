package uk.co.acuminous.julez.event.repository;

import java.util.Collection;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.source.EventSource;

public interface EventRepository extends EventSource {
    int count();    
    Collection<Event> getAll();
    void raise();    
}
