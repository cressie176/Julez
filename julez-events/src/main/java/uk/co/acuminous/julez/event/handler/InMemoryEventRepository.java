package uk.co.acuminous.julez.event.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.repository.BaseEventRepository;
import uk.co.acuminous.julez.event.repository.EventRepository;

public class InMemoryEventRepository extends BaseEventRepository implements EventRepository {

    protected Collection<Event> events;

    public InMemoryEventRepository() {
        events = Collections.synchronizedList(new ArrayList<Event>());
    }
    
    public InMemoryEventRepository(Collection<Event> events) {
        this.events = events;
    }
    
    @Override
    public int count() {
        return events.size();
    }
        
    @Override
    public Collection<Event> getAll() {
        return events;
    }

    @Override
    public void raise() {
        for(Event event : events) {
            handler.onEvent(event);
        }        
    }
    
    @Override
    public void onEvent(Event event) {
        events.add(event);
    }
}
