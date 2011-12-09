package uk.co.acuminous.julez.test;

import java.util.ArrayList;
import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.handler.InMemoryEventRepository;

public class TestEventRepository extends InMemoryEventRepository {

    public void put(Event event) {
        events.add(event);
    }
    
    public Event first() {
        return new ArrayList<Event>(list()).get(0);
    }
    
    public Event get(int i) {
        return new ArrayList<Event>(list()).get(i);
    }
    
    public Event last() {
        return new ArrayList<Event>(list()).get(count() - 1);
    }
    
    public int count(String key, String pattern) {
        return getAll(key, pattern).size();
    }
    
    public List<Event> getAll(String key, String pattern) {
        return list(new EventDataFilter(key, pattern));
    }
}
