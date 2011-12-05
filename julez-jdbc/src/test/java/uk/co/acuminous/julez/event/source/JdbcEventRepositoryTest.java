package uk.co.acuminous.julez.event.source;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.JdbcEventHandler;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class JdbcEventRepositoryTest {


    @Before
    public void init() {
        JdbcTestUtils.ddl();
    }
    
    @After
    public void nuke() throws Exception {
        JdbcTestUtils.nukeDatabase();
    }

    @Test
    public void retriesEventsFromRepositoryInOrder() {
        JdbcEventRepository jdbcEventSource = new JdbcEventRepository(JdbcTestUtils.getDataSource());               
        
        List<Event> expectedEvents = initTestData();

        List<Event> actualEvents = jdbcEventSource.list();
        assertEquals(expectedEvents, actualEvents);
    }
    
    @Test
    public void countsEventsInTheRepository() {
        JdbcEventRepository jdbcEventSource = new JdbcEventRepository(JdbcTestUtils.getDataSource());                
        
        List<Event> events = initTestData();
                
        assertEquals(events.size(), jdbcEventSource.count());
    }
    
    @Test
    public void canOverideSelectQuery() { 
        JdbcEventRepository jdbcEventSource = new JdbcEventRepository(JdbcTestUtils.getDataSource(), "SELECT * FROM EVENT WHERE TYPE='ScenarioEvent/BEGIN'", "");
        
        initTestData();
        
        assertEquals(2, jdbcEventSource.list().size());        
    }
    
    @Test
    public void canOverideCountQuery() {        
        JdbcEventRepository jdbcEventSource = new JdbcEventRepository(JdbcTestUtils.getDataSource(), "", "SELECT COUNT(*) FROM EVENT WHERE TYPE='ScenarioEvent/BEGIN'");
        
        initTestData();
        
        assertEquals(2, jdbcEventSource.count());        
    }    
    
    private List<Event> initTestData() {
        return initTestData(new DateTime());
    }
    
    private List<Event> initTestData(DateTime now) {
                        
        JdbcEventHandler jdbcEventHandler = new JdbcEventHandler(JdbcTestUtils.getDataSource());                
        
        ScenarioRunnerEvent eventA1 = new ScenarioRunnerEvent("A1", now.minusSeconds(8).getMillis(), ScenarioRunnerEvent.BEGIN);        
        ScenarioEvent eventA2 = new ScenarioEvent("A2", now.minusSeconds(7).getMillis(), ScenarioEvent.BEGIN);
        ScenarioRunnerEvent eventB1 = new ScenarioRunnerEvent("B1", now.minusSeconds(6).getMillis(), ScenarioRunnerEvent.BEGIN);        
        ScenarioEvent eventB2 = new ScenarioEvent("B2", now.minusSeconds(5).getMillis(), ScenarioEvent.BEGIN);        
        ScenarioEvent eventA3 = new ScenarioEvent("A3", now.minusSeconds(4).getMillis(), ScenarioEvent.PASS);
        ScenarioEvent eventB3 = new ScenarioEvent("B3", now.minusSeconds(3).getMillis(), ScenarioEvent.FAIL);        
        ScenarioRunnerEvent eventA4 = new ScenarioRunnerEvent("A4", now.minusSeconds(2).getMillis(), ScenarioRunnerEvent.END);        
        ScenarioRunnerEvent eventB4 = new ScenarioRunnerEvent("B4", now.minusSeconds(2).getMillis(), ScenarioRunnerEvent.END);

        List<Event> events = Arrays.asList(eventA1, eventA2, eventB1, eventB2, eventA3, eventB3, eventA4, eventB4);
        for (Event event : events) {
            jdbcEventHandler.onEvent(event);
        }
        
        return events;
    }    
}
