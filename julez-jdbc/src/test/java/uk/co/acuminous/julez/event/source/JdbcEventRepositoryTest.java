package uk.co.acuminous.julez.event.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.jdbc.DefaultEventSql;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.test.JdbcTestUtils;
import uk.co.acuminous.julez.test.TestEventSchema;
import uk.co.acuminous.julez.transformer.DefaultColumnNameTransformer;

public class JdbcEventRepositoryTest {

    private DataSource dataSource;
    private TransformingMapper columnMapper;

    @Before
    public void init() {
        TestEventSchema.ddl();
        
        dataSource = JdbcTestUtils.getDataSource();
        
        String[] persistentProperties = { Event.ID, Event.TIMESTAMP, Event.TYPE };
        columnMapper = new TransformingMapper(new DefaultColumnNameTransformer(), persistentProperties);
    }   
    
    @After
    public void nuke() throws Exception {
        JdbcTestUtils.nukeDatabase();
    }

    @Test
    public void addsScenarioEventsToRepository() {

        ScenarioEvent event = new ScenarioEvent("id", System.currentTimeMillis(), ScenarioEvent.FAIL);

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);        
        repository.onEvent(event);

        List<Event> events = repository.list();
        
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    public void addsScenarioRunnerEventsToRepository() {

        ScenarioRunnerEvent event = new ScenarioRunnerEvent("id", System.currentTimeMillis(), ScenarioRunnerEvent.END);

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);                       
        repository.onEvent(event);

        List<Event> events = repository.list();
        
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    public void ignoresNullRepositoryValues() {

        ScenarioRunnerEvent event = new ScenarioRunnerEvent("id", System.currentTimeMillis(), ScenarioRunnerEvent.END);

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);        
        repository.onEvent(event);

        Event actual = repository.list().get(0);
        
        assertFalse(actual.getData().containsKey("statusCode"));
    }

    @Test
    public void tolleratesUnmappedColumns() {
        
        ScenarioRunnerEvent event = new ScenarioRunnerEvent("id", System.currentTimeMillis(), ScenarioRunnerEvent.END);
        event.put("Foo", "Bar");

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);        
        repository.onEvent(event);

        Event actual = repository.list().get(0);
        
        assertFalse(actual.getData().containsKey("Foo"));
    }    
    
    @Test
    public void listsretriesEventsFromRepositoryInOrder() {
        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);               
        
        List<Event> expectedEvents = initTestData(repository);

        List<Event> actualEvents = repository.list();
        assertEquals(expectedEvents, actualEvents);
    }
    
    @Test
    public void countsEventsInTheRepository() {
        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper);               
        
        List<Event> events = initTestData(repository);
                
        assertEquals(events.size(), repository.count());
    }
    
    @Test
    public void canOverideSelectQuery() {
        
        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper, new DefaultEventSql(columnMapper.getValues()) {
            @Override
            public String getSelectStatement() {
                return "SELECT * FROM EVENT WHERE TYPE='Scenario/begin'";
            }
        });            
        
        initTestData(repository);
        
        assertEquals(2, repository.list().size());        
    }
    
    @Test
    public void canOverideCountQuery() {        

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper, new DefaultEventSql(columnMapper.getValues()) {
            @Override
            public String getCountStatement() {
                return "SELECT count(*) FROM EVENT WHERE TYPE='Scenario/begin'";
            }
        });        
        initTestData(repository);
        
        assertEquals(2, repository.count());        
    } 
    
    @Test
    public void canOverideInsertQuery() {        

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper, new DefaultEventSql(columnMapper.getValues()) {
            @Override
            public String getInsertStatement() {
                return "INSERT INTO event (id, timestamp, type) VALUES (?, ?, ? + '/override')" ;
            }
        });        

        ScenarioRunnerEvent event = new ScenarioRunnerEvent("id", System.currentTimeMillis(), ScenarioRunnerEvent.END);
        repository.onEvent(event);
        
        assertEquals("ScenarioRunner/end/override", repository.list().get(0).getType());        
    }    
        
    @Test
    public void maintainsColumnOrderSpecifiedInSqlStatementProvider() {        

        JdbcEventRepository repository = new JdbcEventRepository(dataSource, columnMapper, new DefaultEventSql("ID", "TYPE", "TIMESTAMP"));        

        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("#TYPE", "type");
        data.put("#TIMESTAMP", "1");
        data.put("#ID", "id");
        
        Event event = new Event(data);
        repository.onEvent(event);
        
        assertEquals(event, repository.list().get(0));
    }        
    
    private List<Event> initTestData(JdbcEventRepository repository) {
                                
        DateTime now = new DateTime();
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
            repository.onEvent(event);
        }
        
        return events;
    }    
}
