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
        
        ScenarioEvent event = new ScenarioEvent("id", System.currentTimeMillis(), ScenarioEvent.FAIL);
        event.getData().put("message", "page not found");
        event.getData().put("statusCode", "404");                
        repository.onEvent(event);
                
        repository.replay();
        
        assertEquals(1, eventMonitor.getEvents().size());
        
        assertEquals(event, eventMonitor.getEvents().get(0));  
    }
    
    @Test
    public void raisesAllEvents() {
        
        List<Event> events = initTestData();        
                
        repository.replay();
        
        assertEquals(8, eventMonitor.getEvents().size());
        for (int i = 0; i < 8; i++) {
            assertEquals(events.get(i), eventMonitor.getEvents().get(i));            
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
    public void raisesAllEventsWithSpecifiedType() {
        initTestData();                
        repository.raiseAllEventsOfType(ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);        
        assertEquals(4, eventMonitor.getEvents().size());          
    } 
    
    @Test
    public void raisesAllEventsAfterSpecifiedTimeWithSpecifiedType() {
        DateTime now = new DateTime();
        
        initTestData(now);        
        
        repository.raiseAllEventsAfterTimestampOfType(now.minusSeconds(8).getMillis(), ScenarioEvent.BEGIN, ScenarioRunnerEvent.BEGIN);
        
        assertEquals(3, eventMonitor.getEvents().size());          
    }   
    
    private List<Event> initTestData() {
        return initTestData(new DateTime());
    }
    
    private List<Event> initTestData(DateTime now) {
                
        ScenarioRunnerEvent eventA1 = new ScenarioRunnerEvent("A1", now.minusSeconds(8).getMillis(), ScenarioRunnerEvent.BEGIN);        
        ScenarioEvent eventA2 = new ScenarioEvent("A2", now.minusSeconds(7).getMillis(), ScenarioEvent.BEGIN);
        ScenarioRunnerEvent eventB1 = new ScenarioRunnerEvent("B1", now.minusSeconds(6).getMillis(), ScenarioRunnerEvent.BEGIN);        
        ScenarioEvent eventB2 = new ScenarioEvent("B2", now.minusSeconds(5).getMillis(), ScenarioEvent.BEGIN);        
        ScenarioEvent eventA3 = new ScenarioEvent("A3", now.minusSeconds(4).getMillis(), ScenarioEvent.PASS);
        ScenarioEvent eventB3 = new ScenarioEvent("B3", now.minusSeconds(3).getMillis(), ScenarioEvent.FAIL);        
        ScenarioRunnerEvent eventA4 = new ScenarioRunnerEvent("A4", now.minusSeconds(2).getMillis(), ScenarioRunnerEvent.END);        
        ScenarioRunnerEvent eventB4 = new ScenarioRunnerEvent("B4", now.minusSeconds(1).getMillis(), ScenarioRunnerEvent.END);

        List<Event> events = Arrays.asList(eventA1, eventA2, eventB1, eventB2, eventA3, eventB3, eventA4, eventB4);
        for (Event event : events) {
            repository.onEvent(event);
        }
        
        return events;
    }    
}
