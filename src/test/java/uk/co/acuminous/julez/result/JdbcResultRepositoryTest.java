package uk.co.acuminous.julez.result;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.test.TestUtils;

public class JdbcResultRepositoryTest {

    private JdbcResultRepository repo;

    @Before
    public void init() throws Exception {
        repo = new JdbcResultRepository(TestUtils.getDataSource());
        repo.ddl();
    }

    @After
    public void nuke() throws Exception {
        TestUtils.nukeDatabase();
    }

    @Test
    public void resultIsAddedToRepository() {
        Result result = new Result("source", "run", "scenario", 1234, ResultStatus.FAIL, "foo");

        repo.add(result);

        assertEquals(1, repo.count());
    }

    @Test
    public void countsAllResultsInRepository() {

        repo.add(new Result("source", "run", "scenario", 1234, ResultStatus.FAIL, "foo"));
        repo.add(new Result("source", "run", "scenario", 1234, ResultStatus.FAIL, "foo"));

        assertEquals(2, repo.count());
    }

    @Test
    public void retrievesAResultFromTheRepository() {
        long timestamp = System.currentTimeMillis();

        Result result = new Result("source", "run", "scenario", timestamp, ResultStatus.FAIL, "foo");

        repo.add(result);

        Result dbResult = repo.get(result.getId());

        assertEquals(result.getId(), dbResult.getId());
        assertEquals("source", dbResult.getSource());
        assertEquals("run", dbResult.getRun());
        assertEquals(timestamp, dbResult.getTimestamp());
        assertEquals("scenario", dbResult.getScenarioName());
        assertEquals(ResultStatus.FAIL, dbResult.getStatus());
        assertEquals("foo", dbResult.getDescription());
    }
}
