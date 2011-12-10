package uk.co.acuminous.julez.event.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.repository.BaseEventRepository;
import uk.co.acuminous.julez.event.repository.EventRepository;

public class InMemoryEventRepository extends BaseEventRepository implements EventRepository {

    protected Collection<Event> events;

    public InMemoryEventRepository() {
        events = new ConcurrentLinkedQueue<Event>();
    }
    
    public InMemoryEventRepository(Collection<Event> events) {
        this.events = events;
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

	@Override public Iterator<Event> iterator() {
		return events.iterator();
	}
}
