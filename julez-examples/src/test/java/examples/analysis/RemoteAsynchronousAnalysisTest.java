package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.scenario.source.ScenarioRepeater.ScenarioRepeaterUnit.REPETITIONS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.EnterpriseTest;
import uk.co.acuminous.julez.test.PassFailErrorScenario;


public class RemoteAsynchronousAnalysisTest extends EnterpriseTest {      
    
    @Test
    public void demonstrateSendingEventsToAJmsDestination() {

        initJmsInfrastructure();
        
        ResultMonitor resultMonitor = new ResultMonitor();
        jmsEventSource.register(resultMonitor);
        
        Scenario scenario = new PassFailErrorScenario();
        scenario.register(jmsEventHandler);        
        ScenarioSource scenarios = new ScenarioRepeater(scenario).limitTo(100, REPETITIONS);                                                                     
        
        new ConcurrentScenarioRunner()
        	.register(jmsEventHandler)
        	.queue(scenarios)
        	.allocate(3, THREADS)
        	.go();
        
        jmsEventSource.shutdownWhenEmpty();
        
        assertEquals(64, resultMonitor.getPassCount());
        assertEquals(25, resultMonitor.getFailureCount());
        assertEquals(11, resultMonitor.getErrorCount());
    }
    
    @Test
    public void demonstratePersistingEventsAsynchronouslyToADatabase() {        
        
        initJmsInfrastructure();
        initDatabaseInfrastructure();    
        
        jmsEventSource.register(jdbcEventRepository);                        
        
        Scenario scenario = new PassFailErrorScenario();
        scenario.register(jmsEventHandler);        
        ScenarioSource scenarios = new ScenarioRepeater(scenario).limitTo(100, REPETITIONS);                                                                     
                
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();                
        runner.register(jmsEventHandler);                                                        
        runner.queue(scenarios).allocate(3, THREADS).go();
        
        jmsEventSource.shutdownWhenEmpty();
        
        assertEquals(302, jdbcEventRepository.count());
        assertEquals(ScenarioRunnerEvent.BEGIN, jdbcEventRepository.getAll().get(0).getType());
        assertEquals(ScenarioRunnerEvent.END, jdbcEventRepository.getAll().get(301).getType());
    }
}
