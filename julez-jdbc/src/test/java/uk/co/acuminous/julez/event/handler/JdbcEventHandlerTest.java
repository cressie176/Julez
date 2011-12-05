package uk.co.acuminous.julez.event.handler;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JdbcTestUtils;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.source.JdbcEventRepository;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class JdbcEventHandlerTest {

    private JdbcEventRepository jdbcEventSource;
    private DataSource dataSource;

    @Before
    public void init() {
        JdbcTestUtils.ddl();
        
        dataSource = JdbcTestUtils.getDataSource();        
        
        jdbcEventSource = new JdbcEventRepository(JdbcTestUtils.getDataSource());        
    }
    
    @After
    public void nuke() throws Exception {
        JdbcTestUtils.nukeDatabase();
    }

    @Test
    public void addsEventsToRepository() {
        
        ScenarioEvent event = new ScenarioEvent("id", System.currentTimeMillis(), ScenarioEvent.FAIL);
        event.getData().put("message", "page not found");
        event.getData().put("statusCode", "404");

        new JdbcEventHandler(dataSource).onEvent(event);                
        
        List<Event> events = jdbcEventSource.list();        
        assertEquals(1, events.size());        
        assertEquals(event, events.get(0));  
    }
}
