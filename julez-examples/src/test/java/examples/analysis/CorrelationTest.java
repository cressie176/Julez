package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.filter.EventFilter;
import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.EventPipe;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.event.source.JdbcEventRepository;
import uk.co.acuminous.julez.jdbc.DefaultEventSql;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.EnterpriseTest;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.PassFailErrorScenario;
import uk.co.acuminous.julez.test.TestEventRepository;
import uk.co.acuminous.julez.transformer.DefaultColumnNameTransformer;

public class CorrelationTest extends EnterpriseTest {
    
    @Test
    public void demonstrateRealtimeCorrelationOfTestResults() throws UnknownHostException {
                
        String testRun1 = "A";        
        String testClient1 = "192.168.1.1";        
        
        String testRun2 = "B";        
        String testClient2 = "192.168.1.2";        
        
        TestEventRepository unfilteredRepository = new TestEventRepository();
        TestEventRepository filteredRepository = new TestEventRepository();

        EventFilter testClientFilter = new EventDataFilter().filterEventsWhere("TEST_CLIENT").matches(testClient2).register(filteredRepository);    
        EventFilter testRunFilter = new EventDataFilter().filterEventsWhere("TEST_RUN").matches(testRun1).register(testClientFilter);
        
        FanOutPipe monitors = new FanOutPipe(unfilteredRepository, testRunFilter);
       
        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(
            initTestRun(testRun1, testClient1, monitors),
            initTestRun(testRun1, testClient2, monitors),
            initTestRun(testRun2, testClient1, monitors),
            initTestRun(testRun2, testClient2, monitors)
        );        
        runner.go();
        
        int uncorrelatedEvents = unfilteredRepository.count();
        int correlatedEvents = filteredRepository.count();
        
        assertEquals(correlatedEvents * 4, uncorrelatedEvents);
    }
        
    @Test
    public void demonstratePosthumousResultAnalysis() {        
        
        initDatabaseInfrastructure();
        
        ThroughputMonitor throughputMonitor1 = new ThroughputMonitor();
        
        EventPipe fanoutPipe = new FanOutPipe().registerAll(throughputMonitor1, jdbcEventRepository);

        Scenario scenario = new NoOpScenario().register(fanoutPipe);
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(100, SCENARIOS);        

        new ConcurrentScenarioRunner().register(fanoutPipe).allocate(10, THREADS).queue(scenarios).go();

        ThroughputMonitor throughputMonitor2 = new ThroughputMonitor();
        jdbcEventRepository.register(throughputMonitor2);
        jdbcEventRepository.raise();

        assertMinimumThroughput(throughputMonitor1.getThroughput(), throughputMonitor2.getThroughput());
    }  
    
    @Test
    public void demonstrateTrendingUsingFilters() {
        
        initDatabaseInfrastructure();
        
        System.out.println("\nTrending Using Filters\n-----------------");            
        
        for (String testRun : Arrays.asList("A", "B", "C")) {
            
            initTestRun(testRun, jdbcEventRepository).go();
            
            ThroughputMonitor monitor = new ThroughputMonitor();
            
            EventFilter filter = new EventDataFilter().filterEventsWhere("TEST_RUN").matches(testRun).register(monitor);
            
            jdbcEventRepository.register(filter).raise();

            System.out.println(String.format("TestRun %s: %d", testRun, monitor.getThroughput()));            
        }
    }
    
    @Test
    public void demonstrateTrendingUsingSql() {
        
        initDatabaseInfrastructure();

        System.out.println("\nTrending Using SQL\n-----------------");      
        
        for (String testRun : Arrays.asList("A", "B", "C")) {
            
            initTestRun(testRun, "", jdbcEventRepository).go();
            
            TransformingMapper columnMapper = getColumnMapper();
            CorrelatingEventSql sql = new CorrelatingEventSql(columnMapper.getValues(), testRun);
            jdbcEventRepository = new JdbcEventRepository(dataSource, columnMapper, sql);
            
            ThroughputMonitor monitor = new ThroughputMonitor();  
            
            jdbcEventRepository.register(monitor).raise();

            System.out.println(String.format("TestRun %s: %d", testRun, monitor.getThroughput()));
        }
    }
     
    private ConcurrentScenarioRunner initTestRun(String testRun, EventHandler monitor) {
        return initTestRun(testRun, "", monitor);
    }
    
    private ConcurrentScenarioRunner initTestRun(String testRun, String testClient, EventHandler monitor) {
        Map<String, String> correlationData = new HashMap<String, String>();
        correlationData.put("TEST_RUN", testRun);
        correlationData.put("TEST_CLIENT", testClient);
        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationData);        
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationData);

        PassFailErrorScenario scenario = new PassFailErrorScenario();
        scenario.useEventFactory(scenarioEventFactory);
        scenario.register(monitor);        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(100, SCENARIOS);                                                                     

        return new ConcurrentScenarioRunner()
            .useEventFactory(scenarioRunnerEventFactory)
            .register(monitor)
            .queue(scenarios);
    }    
    
    @Override    
    protected void createEventTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute(
            "CREATE TABLE event (" +
            "id VARCHAR(36), " +
            "timestamp VARCHAR(255), " +
            "type VARCHAR(255), " +
            "TEST_RUN VARCHAR(255), " +
            "TEST_CLIENT VARCHAR(255), " +
            "PRIMARY KEY (id)" +
        ")");
    }

    @Override
    protected TransformingMapper getColumnMapper() {
        return new TransformingMapper(new DefaultColumnNameTransformer(), Event.ID, Event.TIMESTAMP, Event.TYPE, "TEST_RUN", "TEST_CLIENT");
    }     
    
    class CorrelatingEventSql extends DefaultEventSql {

        private final String testRun;

        CorrelatingEventSql(Collection<String> columnNames, String testRun) {
            super(columnNames);
            this.testRun = testRun;
        }
        
        @Override
        public String getSelectStatement() {
            return String.format("SELECT * FROM event WHERE test_run='%s' ORDER BY timestamp ASC, id ASC", testRun);
        }

        @Override
        public String getInsertStatement() {
            return "INSERT INTO event (id, timestamp, type, test_run, test_client) values (?, ?, ?, ?, ?)";
        }
    }
}
