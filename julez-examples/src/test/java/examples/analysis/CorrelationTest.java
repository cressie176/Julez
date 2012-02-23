package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;
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
import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.EventPipe;
import uk.co.acuminous.julez.event.pipe.FanOutEventPipe;
import uk.co.acuminous.julez.event.source.BigGulpJdbcEventRepository;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.executor.SynchronousScenarioExecutor;
import uk.co.acuminous.julez.jdbc.DefaultEventSql;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.instruction.ScenarioRunnerStarter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.EnterpriseTest;
import uk.co.acuminous.julez.test.PassFailErrorScenario;
import uk.co.acuminous.julez.test.TestEventRepository;
import uk.co.acuminous.julez.transformer.DefaultColumnNameTransformer;

public class CorrelationTest extends EnterpriseTest {
    
    @Test
    public void demonstrateRealtimeCorrelationOfTestResultsGeneratedByMultipleScenarioRunners() throws UnknownHostException {
                
        String testRun1 = "A";        
        String testClient1 = "192.168.1.1";        
        
        String testRun2 = "B";        
        String testClient2 = "192.168.1.2";        
        
        TestEventRepository unfilteredRepository = new TestEventRepository();
        TestEventRepository filteredRepository = new TestEventRepository();

        EventFilter filterOnTestClient2 = new EventDataFilter().filterEventsWhere("TEST_CLIENT").matches(testClient2);    
        EventFilter filterOnTestRun1 = new EventDataFilter().filterEventsWhere("TEST_RUN").matches(testRun1);
        
        filterOnTestClient2.register(filteredRepository);
        filterOnTestRun1.register(filterOnTestClient2);
        
        FanOutEventPipe monitors = new FanOutEventPipe(unfilteredRepository, filterOnTestRun1);
               
        ScenarioSource concurrentScenarios = new ScenarioHopper(
            new ScenarioRunnerStarter(initScenarioRunner(testRun1, testClient1, monitors)),
            new ScenarioRunnerStarter(initScenarioRunner(testRun1, testClient2, monitors)),
            new ScenarioRunnerStarter(initScenarioRunner(testRun2, testClient1, monitors)),
            new ScenarioRunnerStarter(initScenarioRunner(testRun2, testClient2, monitors))                
        );
        
        ScenarioExecutor executor = new SynchronousScenarioExecutor();
        
        new SimpleScenarioRunner().assign(executor).queue(concurrentScenarios).start();
        
        int uncorrelatedEvents = unfilteredRepository.count();
        int correlatedEvents = filteredRepository.count();
        
        assertEquals(correlatedEvents * 4, uncorrelatedEvents);
    }
        
    @Test
    public void demonstratePosthumousResultAnalysis() {        
        
        initDatabaseInfrastructure();
        
        ScenarioThroughputMonitor throughputMonitor1 = new ScenarioThroughputMonitor();
        
        EventPipe realTimeEventHandlers = new FanOutEventPipe().registerAll(throughputMonitor1, jdbcEventRepository);

        Scenario scenario = new NoOpScenario().register(realTimeEventHandlers);
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(100, TIMES);        

        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(10, THREADS);        
        
        new SimpleScenarioRunner().assign(executor).register(realTimeEventHandlers).queue(scenarios).start();        
        
        ScenarioThroughputMonitor posthumousEventHandler = new ScenarioThroughputMonitor();
        jdbcEventRepository.register(posthumousEventHandler);
        jdbcEventRepository.raise();

        assertMinimumThroughput(throughputMonitor1.getThroughput(), posthumousEventHandler.getThroughput());
    }  
    
    @Test
    public void demonstrateTrendingUsingFilters() {
        
        initDatabaseInfrastructure();
        
        System.out.println("\nTrending Using Filters\n-----------------");            
        
        for (String testRun : Arrays.asList("A", "B", "C")) {
            
            initTestRun(testRun, jdbcEventRepository).start();
            
            ScenarioThroughputMonitor monitor = new ScenarioThroughputMonitor();
            
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
            
            initScenarioRunner(testRun, "", jdbcEventRepository).start();
            
            TransformingMapper columnMapper = getColumnMapper();
            CorrelatingEventSql sql = new CorrelatingEventSql(columnMapper.getValues(), testRun);
            jdbcEventRepository = new BigGulpJdbcEventRepository(dataSource, columnMapper, sql);
            
            ScenarioThroughputMonitor monitor = new ScenarioThroughputMonitor();  
            
            jdbcEventRepository.register(monitor).raise();

            System.out.println(String.format("TestRun %s: %d", testRun, monitor.getThroughput()));
        }
    }
     
    private ScenarioRunner initTestRun(String testRun, EventHandler monitor) {
        return initScenarioRunner(testRun, "", monitor);
    }
    
    private ScenarioRunner initScenarioRunner(String testRun, String testClient, EventHandler monitor) {
        Map<String, String> correlationData = new HashMap<String, String>();
        correlationData.put("TEST_RUN", testRun);
        correlationData.put("TEST_CLIENT", testClient);
        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationData);        
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationData);

        PassFailErrorScenario scenario = new PassFailErrorScenario();
        scenario.useEventFactory(scenarioEventFactory);
        scenario.register(monitor);        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(100, TIMES);                                                                     

        ScenarioExecutor executor = new SynchronousScenarioExecutor();
        
        return new SimpleScenarioRunner()
            .useEventFactory(scenarioRunnerEventFactory)
            .assign(executor)
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
