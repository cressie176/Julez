package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
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
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);                                                             
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();                
        runner.register(jmsEventHandler);                                                        
        runner.queue(scenarios).allocate(3, THREADS).go();
        
        // Ensure the queue is drained
        jmsEventSource.shutdownGracefully();
        
        assertEquals(64, resultMonitor.getPassCount());
        assertEquals(25, resultMonitor.getFailureCount());
        assertEquals(11, resultMonitor.getErrorCount());
    }
    
    @Test
    public void demonstratePersistingEventsAsynchronouslyToADatabase() {        
        
        initJmsInfrastructure();
        initDatabaseInfrastructure();    
        jmsEventSource.register(jdbcEventHandler);                        
        
        Scenario scenario = new PassFailErrorScenario();
        scenario.register(jmsEventHandler);        
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);                                                             
                
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();                
        runner.register(jmsEventHandler);                                                        
        runner.queue(scenarios).allocate(3, THREADS).go();
        
        // Ensure the queue is drained
        jmsEventSource.shutdownGracefully();
        
        assertEquals(302, jdbcEventRepository.count());
        assertEquals(ScenarioRunnerEvent.BEGIN, jdbcEventRepository.list().get(0).getType());
        assertEquals(ScenarioRunnerEvent.END, jdbcEventRepository.list().get(301).getType());
    }
}
