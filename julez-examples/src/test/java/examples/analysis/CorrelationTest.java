package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.event.source.JdbcEventRepository;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.marshalling.NamespaceBasedEventClassResolver;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
import uk.co.acuminous.julez.test.EnterpriseTest;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.PassFailErrorScenario;
import uk.co.acuminous.julez.transformer.DefaultColumnNameTransformer;

public class CorrelationTest extends EnterpriseTest {
    
    @Test
    public void demonstrateRealtimeCorrelationOfTestResults() throws UnknownHostException {
                
        String testRun1 = "A";        
        String testClient1 = "192.168.1.1";        
        
        String testRun2 = "B";        
        String testClient2 = "192.168.1.2";        
        
        EventMonitor unfilteredMonitor = new EventMonitor();
        EventMonitor filteredMonitor = new EventMonitor();
        
        EventDataFilter filter1 = new EventDataFilter("TEST_RUN", testRun1);
        EventDataFilter filter2 = new EventDataFilter("TEST_CLIENT", testClient2);
        
        filter1.register(filter2);
        filter2.register(filteredMonitor);        
        FanOutPipe monitors = new FanOutPipe(unfilteredMonitor, filter1);
       
        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(
            initTestRun(testRun1, testClient1, monitors),
            initTestRun(testRun1, testClient2, monitors),
            initTestRun(testRun2, testClient1, monitors),
            initTestRun(testRun2, testClient2, monitors)
        );
        
        runner.go();
        
        int uncorrelatedEvents = unfilteredMonitor.getEvents().size();
        int correlatedEvents = filteredMonitor.getEvents().size();
        assertEquals(correlatedEvents * 4, uncorrelatedEvents);
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
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);                                                             

        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();  
        runner.useEventFactory(scenarioRunnerEventFactory);
        runner.register(monitor);
        runner.queue(scenarios);
        
        return runner;
    }
    
    
    @Test
    public void demonstratePosthumousResultAnalysis() {        
        
        initDatabaseInfrastructure();
        
        ThroughputMonitor throughputMonitor1 = new ThroughputMonitor();
        FanOutPipe fanoutPipe = new FanOutPipe();
        fanoutPipe.registerAll(throughputMonitor1, jdbcEventHandler);

        Scenario scenario = new NoOpScenario();
        scenario.register(fanoutPipe);
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);

        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(fanoutPipe);
        runner.queue(scenarios).allocate(10, THREADS).go();

        ThroughputMonitor throughputMonitor2 = new ThroughputMonitor();
        jdbcEventRepository.register(throughputMonitor2);
        jdbcEventRepository.raise();

        assertMinimumThroughput(throughputMonitor1.getThroughput(), throughputMonitor2.getThroughput());
    }  
    
    @Test
    public void demonstrateTrendingUsingFilters() {
        
        initDatabaseInfrastructure();
        
        System.out.println("\nTrending Using Filters\n-----------------");            
        
        for (String testRun : new String[] { "A", "B", "C" }) {
            
            initTestRun(testRun, "", jdbcEventHandler).go();
            
            ThroughputMonitor monitor = new ThroughputMonitor();
            EventDataFilter filter = new EventDataFilter("TEST_RUN", testRun);
            filter.register(monitor);
            
            jdbcEventRepository.register(filter);
            jdbcEventRepository.raise();

            System.out.println(String.format("TestRun %s: %d", testRun, monitor.getThroughput()));            
        }
    }
    
    @Test
    public void demonstrateTrendingUsingSql() {
        
        initDatabaseInfrastructure();

        System.out.println("\nTrending Using SQL\n-----------------");      
        
        for (String testRun : new String[] { "A", "B", "C" }) {
            
            initTestRun(testRun, "", jdbcEventHandler).go();
            
            jdbcEventRepository = new JdbcEventRepository(dataSource, getColumnMapper(), new NamespaceBasedEventClassResolver(), 
                    String.format("SELECT * FROM event WHERE test_run='%s' ORDER BY timestamp ASC, id ASC", testRun),
                    String.format("SELECT COUNT(*) FROM event WHERE test_run='%s'", testRun));
                    
            ThroughputMonitor monitor = new ThroughputMonitor();        
            jdbcEventRepository.register(monitor);
            jdbcEventRepository.raise();

            System.out.println(String.format("TestRun %s: %d", testRun, monitor.getThroughput()));
        }
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
}
