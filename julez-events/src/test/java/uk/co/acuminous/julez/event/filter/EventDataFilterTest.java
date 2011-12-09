package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;

public class EventDataFilterTest {

    private TestEventRepository repository;

    @Before
    public void init() {
        repository = new TestEventRepository();        
    }
    
    @Test
    public void excludesEventsWithWrongNamespace() {
        
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/1").register(repository);
        
        filter.onEvent(new Event("B/1"));
        
        assertEquals(0, repository.count());
    } 
    
    @Test
    public void excludesEventsWithWrongLocalName() {
        
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/1").register(repository);
        
        filter.onEvent(new Event("A/2"));
        
        assertEquals(0, repository.count());
    }    

    @Test
    public void includesEventsWithMatchingNamespace() {     
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/.*").register(repository);
        
        Event wantedEvent = new Event("A/foo");
        
        filter.onEvent(wantedEvent);
        filter.onEvent(new Event("B/foo"));        
        
        assertEquals(1, repository.count());
        assertEquals(wantedEvent, repository.first());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/1").register(repository);
        
        Event wantedEvent = new Event("A/1");
        
        filter.onEvent(wantedEvent);                
        filter.onEvent(new Event("Wanted/bar"));
        
        assertEquals(1, repository.count());
        assertEquals(wantedEvent, repository.first());
    }
    
    @Test
    public void includesMultipleMatchingEvents() {     
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/.*").register(repository);
        
        Event wantedEvent1 = new Event("A/1");
        Event wantedEvent2 = new Event("A/2");
        Event wantedEvent3 = new Event("A/3");
        
        filter.onEvent(wantedEvent1);
        filter.onEvent(wantedEvent2);
        filter.onEvent(wantedEvent3);        
        filter.onEvent(new Event("B/1"));
        
        assertEquals(3, repository.count());
        assertEquals(wantedEvent1, repository.get(0));
        assertEquals(wantedEvent2, repository.get(1));
        assertEquals(wantedEvent3, repository.get(2));
    }    
    
    @Test
    public void filtersCollections() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "A/1");
        
        Event wantedEvent = new Event("A/1");
        
        repository.put(wantedEvent);        
        repository.put(new Event("A/2"));        
        
        List<Event> filteredEvents = filter.applyTo(repository.list());  
        
        assertEquals(1, filteredEvents.size());        
        assertEquals(wantedEvent, filteredEvents.get(0));
    }
}
