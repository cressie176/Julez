package uk.co.acuminous.julez.event.filter;

import static org.junit.Assert.assertEquals;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;
import uk.co.acuminous.julez.test.TestUtils;

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
        
        assertEquals(0, TestUtils.countEvents(repository));
    } 
    
    @Test
    public void excludesEventsWithWrongLocalName() {
        
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/1").register(repository);
        
        filter.onEvent(new Event("A/2"));
        
        assertEquals(0, TestUtils.countEvents(repository));
    }    

    @Test
    public void includesEventsWithMatchingNamespace() {     
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/.*").register(repository);
        
        Event wantedEvent = new Event("A/foo");
        
        filter.onEvent(wantedEvent);
        filter.onEvent(new Event("B/foo"));        
        
        assertEquals(1, TestUtils.countEvents(repository));
        assertEquals(wantedEvent, repository.first());
    }    
    
    @Test
    public void includesEventsWithMatchingBaseTypeAndSubType() {     
        EventFilter filter = new EventDataFilter(Event.TYPE, "A/1").register(repository);
        
        Event wantedEvent = new Event("A/1");
        
        filter.onEvent(wantedEvent);                
        filter.onEvent(new Event("Wanted/bar"));
        
        assertEquals(1, TestUtils.countEvents(repository));
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
        
        Assert.assertTrue(TestUtils.checkEvents(new Event[] { 
        		wantedEvent1, wantedEvent2, wantedEvent3}, 
        		repository));
    }    
    
    @Test
    public void filtersCollections() {
        EventDataFilter filter = new EventDataFilter(Event.TYPE, "A/1");
        
        Event wantedEvent = new Event("A/1");
        
        repository.put(wantedEvent);        
        repository.put(new Event("A/2"));        
        
        List<Event> filteredEvents = filter.applyTo(repository);  
        
        assertEquals(1, filteredEvents.size());        
        assertEquals(wantedEvent, filteredEvents.get(0));
    }
}
