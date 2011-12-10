package uk.co.acuminous.julez.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class EventTest {

    @Test
    public void verboseConvenienceConstructorInitialisesEventCorrectly() {
        Event event = new Event("id", 1L, "type");
        
        assertEquals("id", event.get(Event.ID));
        assertEquals("1", event.get(Event.TIMESTAMP));
        assertEquals("type", event.get(Event.TYPE));
    }
    
    @Test
    public void simpleConvenienceConstructorInitialisesEventCorrectly() {
        Event event = new Event("type");
        
        assertNotNull(event.get(Event.ID));
        assertNotNull("1", event.get(Event.TIMESTAMP));
        assertEquals("type", event.get(Event.TYPE));
    }    
    
    @Test
    public void assertMapContructorIntialisesEventCorrectly() {
        Map<String, String> data = new HashMap<String, String>();
        Event event = new Event(data);
        
        assertSame(data, event.getData());
    }
    
    @Test
    public void assertDataAccessorsReturnCorrectValues() {
        Event event = new Event("id", 1L, "type");
        event.put("Foo", "Bar");
        
        assertEquals("id", event.getId());
        assertEquals(1L, event.getTimestamp());
        assertEquals("type", event.getType());
        assertEquals("Bar", event.get("Foo"));        
    }

    @Test
    public void toStringIncludesClassName() {
        Event event = new Event("type");        
        String text = event.toString();
        assertTrue("Class name not found in Event.toString()", text.startsWith(event.getClass().getName()));
    }    
    
    @Test
    public void toStringEvenIncludesClassNameForAnonymousEvents() {
        Event event = new Event("type") {};        
        String text = event.toString();
        assertTrue("Class name not found in Event.toString()", text.startsWith(event.getClass().getName()));
    }    
    
    @Test
    public void toStringSortsEventDataConsistently() {
        Event event = new Event("id", 1L, "type");
        event.getData().put("A", "Last");
        event.getData().put("!Z", "First");
        String text = event.toString().replace(event.getClass().getName(), "");
        assertEquals("{!Z=First, #ID=id, #TIMESTAMP=1, #TYPE=type, A=Last}", text);
    }
    
    @Test
    public void equalsReturnsTrueWhenTwoEventsAreEquivalent() {
        Event event1 = new Event(new HashMap<String, String>());
        Event event2 = new Event(new TreeMap<String, String>());
       
       assertEquals(event1, event2); 
       
       event1.put("a", "1");
       event2.put("a", "1");
       assertEquals(event1, event2);
    }
    
    @Test
    public void equalsReturnsTrueUsingDifferentUnderlyingMapImplementations() {
       Event event1 = new Event(new HashMap<String, String>());
       Event event2 = new Event(new TreeMap<String, String>());
       
       assertEquals(event1, event2); 
       
       event1.put("a", "1");
       event2.put("a", "1");
       assertEquals(event1, event2);
    }    
    
    @Test
    public void equalsReturnsFalseWhenTwoEventsAreEquivalent() {
        Event event1 = new Event(new HashMap<String, String>());
        Event event2 = new Event(new TreeMap<String, String>());
        
        event1.put("a", "1");
        assertFalse(String.format("%s != %s", event1, event2), event1.equals(event2));
        
        event2.put("a", "1");
        event2.put("b", "2");
        assertFalse(String.format("%s != %s", event1, event2), event1.equals(event2));        
    }
}
