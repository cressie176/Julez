package uk.co.acuminous.julez.event.repository;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.repository.ScenarioEventJdbcRepository;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.TestUtils;

public class JdbcResultRepositoryTest {

    private ScenarioEventJdbcRepository repository;
    private ScenarioEventFactory scenarioEventFactory;

    @Before
    public void init() throws Exception {
        repository = new ScenarioEventJdbcRepository(TestUtils.getDataSource());
        repository.ddl();
        
        scenarioEventFactory = new ScenarioEventFactory("foo");        
    }

    @After
    public void nuke() throws Exception {
        TestUtils.nukeDatabase();
    }

    @Test
    public void eventIsAddedToRepository() {
        repository.add(scenarioEventFactory.fail());
        assertEquals(1, repository.count());
    }

    @Test
    public void countsAllEventsInRepository() {
        repository.add(scenarioEventFactory.fail());
        repository.add(scenarioEventFactory.fail());
        repository.add(scenarioEventFactory.fail());
        assertEquals(3, repository.count());
    }

    @Test
    public void retrievesAnEventFromTheRepository() {

        long timestamp = System.currentTimeMillis();

        ScenarioEvent event = new ScenarioEvent("id", timestamp, ScenarioEvent.FAIL, "foo");

        repository.add(event);

        ScenarioEvent dbEvent = repository.get("id");

        assertEquals("id", dbEvent.getId());
        assertEquals(timestamp, dbEvent.getTimestamp());
        assertEquals(ScenarioEvent.FAIL, dbEvent.getType());
        assertEquals("foo", dbEvent.getCorrelationId());
    }    
}
