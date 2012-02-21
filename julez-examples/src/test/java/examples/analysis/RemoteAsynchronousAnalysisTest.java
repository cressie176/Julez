package examples.analysis;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.EnterpriseTest;
import uk.co.acuminous.julez.test.PassFailErrorScenario;
import uk.co.acuminous.julez.test.TestUtils;


public class RemoteAsynchronousAnalysisTest extends EnterpriseTest {      
    
    @Test
    public void demonstrateSendingEventsToAJmsDestination() {

        initJmsInfrastructure();
        
        ScenarioResultMonitor resultMonitor = new ScenarioResultMonitor();
        jmsEventSource.register(resultMonitor);
        
        Scenario scenario = new PassFailErrorScenario().register(jmsEventHandler);        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).atMost(100, TIMES);                                                                     
        
        new ConcurrentScenarioRunner().register(jmsEventHandler).queue(scenarios).allocate(4, THREADS).start();
        
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
        
        Scenario scenario = new PassFailErrorScenario().register(jmsEventHandler);        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).atMost(100, TIMES);                                                                     
                
        new ConcurrentScenarioRunner().register(jmsEventHandler).allocate(4, THREADS).queue(scenarios).start();
        
        jmsEventSource.shutdownWhenEmpty();
        
        assertEquals(302, TestUtils.countEvents(jdbcEventRepository));
        assertEquals(ScenarioRunnerEvent.BEGIN, TestUtils.first(jdbcEventRepository).getType());
        assertEquals(ScenarioRunnerEvent.END, TestUtils.last(jdbcEventRepository).getType());
    }
}
