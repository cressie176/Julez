package uk.co.acuminous.julez.scenario.event;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.repository.ScenarioEventJdbcRepository;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.test.TestUtils;

public class JdbcResultRepositoryTest {

    private ScenarioEventJdbcRepository repository;

    @Before
    public void init() throws Exception {
        repository = new ScenarioEventJdbcRepository(TestUtils.getDataSource());
        repository.ddl();
    }

    @After
    public void nuke() throws Exception {
        TestUtils.nukeDatabase();
    }

    @Test
    public void eventIsAddedToRepository() {
        repository.add(ScenarioEvent.fail());
        assertEquals(1, repository.count());
    }

    @Test
    public void countsAllEventsInRepository() {
        repository.add(ScenarioEvent.fail());
        repository.add(ScenarioEvent.fail());
        repository.add(ScenarioEvent.pass());
        assertEquals(3, repository.count());
    }

    @Test
    public void retrievesAnEventFromTheRepository() {

        long timestamp = System.currentTimeMillis();

        ScenarioEvent event = new ScenarioEvent("id", timestamp, ScenarioEvent.FAIL);

        repository.add(event);

        ScenarioEvent dbEvent = repository.get(event.getId());

        assertEquals(event.getId(), dbEvent.getId());
        assertEquals(timestamp, dbEvent.getTimestamp());
        assertEquals(ScenarioEvent.FAIL, dbEvent.getType());
    }    
}
