package uk.co.acuminous.julez.event.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class JdbcScenarioEventRepositoryTest {

    private JdbcEventRepository repository;
    private EventMonitor eventMonitor;

    @Before
    public void init() throws Exception {
        repository = new JdbcEventRepository(JdbcTestUtils.getDataSource());
        repository.ddl();
        
        eventMonitor = new EventMonitor(); 
        repository.register(eventMonitor);        
    }

    @After
    public void nuke() throws Exception {
        JdbcTestUtils.nukeDatabase();
    }

    @Test
    public void eventIsAddedToRepository() {
        
        ScenarioEvent event = new ScenarioEvent("id", System.currentTimeMillis(), ScenarioEvent.FAIL, "correlation");
        event.getData().put("message", "page not found");
        event.getData().put("statusCode", "404");                
        repository.onEvent(event);
                
        repository.raiseAllEvents();
        
        assertEquals(1, eventMonitor.getEvents().size());
        
        assertEquals(event, eventMonitor.getEvents().get(0));  
    }
    
    @Test
    public void raisesAllEvents() {
        
        List<Event> events = initTestData();        
                
        repository.raiseAllEvents();
        
        assertEquals(8, eventMonitor.getEvents().size());
        for (int i = 0; i < 8; i++) {
            assertEquals(events.get(i), eventMonitor.getEvents().get(i));            
        }      
    }
    
    @Test
    public void raisesAllCorrelectedEvents() {
                
        initTestData();        
        
        repository.raiseCorrelatedEvents("A");
        
        List<Event> events = eventMonitor.getEvents();
        
        assertEquals(4, events.size());
        for (Event event : eventMonitor.getEvents()) {
            assertEquals("A", event.getCorrelationId());           
        }
    }
    
    @Test
    public void raisesAllEventsAfterASpecifiedTime() {
        
        DateTime now = new DateTime();        
        initTestData(now);        
        
        repository.raiseAllEventsAfter(now.minusSeconds(5).getMillis());
        
        assertEquals(4, eventMonitor.getEvents().size());        
    }
    
    @Test
    public void raisesCorrelatedEventsAfterASpecifiedTime() {
        
        DateTime now = new DateTime();        
        initTestData(now);        
        
        repository.raiseCorrelatedEventsAfter("A", now.minusSeconds(5).getMillis());
        
        assertEquals(2, eventMonitor.getEvents().size());   
    } 
    
    @Test
    public void raisesAllEventsWithSpecifiedType() {
        initTestData();                
        repository.raiseAllEventsOfType(ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);        
        assertEquals(4, eventMonitor.getEvents().size());          
    }
    
    @Test
    public void raisesCorrelatedEventsWithSpecifiedType() {        
        initTestData();                
        repository.raiseAllCorrelatedEventsOfType("A", ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);        
        assertEquals(2, eventMonitor.getEvents().size());          
    }  
    
    @Test
    public void raisesAllEventsAfterSpecifiedTimeWithSpecifiedType() {
        DateTime now = new DateTime();
        
        initTestData(now);        
        
        repository.raiseAllEventsAfterTimestampOfType(now.minusSeconds(8).getMillis(), ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);
        
        assertEquals(3, eventMonitor.getEvents().size());          
    }
    
    @Test
    public void raisesCorreatedEventsAfterSpecifiedTimeWithSpecifiedType() {
        DateTime now = new DateTime();
        
        initTestData(now);        
        
        repository.raiseCorrelatedEventsAfterTimestampOfType("A", now.minusSeconds(8).getMillis(), ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);
        
        assertEquals(1, eventMonitor.getEvents().size());          
    }    
    
    private List<Event> initTestData() {
        return initTestData(new DateTime());
    }
    
    private List<Event> initTestData(DateTime now) {
                
        ScenarioRunnerEvent eventA1 = new ScenarioRunnerEvent("A1", now.minusSeconds(8).getMillis(), ScenarioRunnerEvent.BEGIN, "A");        
        ScenarioEvent eventA2 = new ScenarioEvent("A2", now.minusSeconds(7).getMillis(), ScenarioEvent.BEGIN, "A");
        ScenarioRunnerEvent eventB1 = new ScenarioRunnerEvent("B1", now.minusSeconds(6).getMillis(), ScenarioRunnerEvent.BEGIN, "B");        
        ScenarioEvent eventB2 = new ScenarioEvent("B2", now.minusSeconds(5).getMillis(), ScenarioEvent.BEGIN, "B");        
        ScenarioEvent eventA3 = new ScenarioEvent("A3", now.minusSeconds(4).getMillis(), ScenarioEvent.PASS, "A");
        ScenarioEvent eventB3 = new ScenarioEvent("B3", now.minusSeconds(3).getMillis(), ScenarioEvent.FAIL, "B");        
        ScenarioRunnerEvent eventA4 = new ScenarioRunnerEvent("A4", now.minusSeconds(2).getMillis(), ScenarioRunnerEvent.END, "A");        
        ScenarioRunnerEvent eventB4 = new ScenarioRunnerEvent("B4", now.minusSeconds(1).getMillis(), ScenarioRunnerEvent.END, "B");

        List<Event> events = Arrays.asList(eventA1, eventA2, eventB1, eventB2, eventA3, eventB3, eventA4, eventB4);
        for (Event event : events) {
            repository.onEvent(event);
        }
        
        return events;
    }    
}
