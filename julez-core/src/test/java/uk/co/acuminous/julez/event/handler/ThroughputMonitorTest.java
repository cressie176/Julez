package uk.co.acuminous.julez.event.handler;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ThroughputMonitorTest {

    private ScenarioEventFactory scenarioEventFactory;
    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;

    @Before
    public void init() {
        scenarioEventFactory = new ScenarioEventFactory();
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();
    }
    
    @Test
    public void calculatesThroughputFromPasses() {
        assertThroughput(scenarioEventFactory.pass());
    }
    
    @Test
    public void calculatesThroughputFromFailures() {
        assertThroughput(scenarioEventFactory.fail());
    }
        
    @Test
    public void calculatesThroughputFromErrors() {
        assertThroughput(scenarioEventFactory.error());
    }    
    
    @Test
    public void calculatesThroughputForVeryQuickScenarios() {
        ThroughputMonitor monitor = new ThroughputMonitor();
        
        monitor.onEvent(scenarioRunnerEventFactory.begin());
        monitor.onEvent(scenarioEventFactory.begin());
        monitor.onEvent(scenarioEventFactory.pass());
        monitor.onEvent(scenarioRunnerEventFactory.end());
        
        assertFalse("Throughput was not calculated for extremely quick scenario", 0 == monitor.getThroughput());
    }  
    
    private void assertThroughput(ScenarioEvent event) {
        ThroughputMonitor monitor = new ThroughputMonitor();
        
        assertEquals(0, monitor.getThroughput());
        
        monitor.onEvent(scenarioRunnerEventFactory.begin());
        
        for (int i = 0; i < 10; i++) {
            monitor.onEvent(scenarioEventFactory.begin());            
            monitor.onEvent(event);
        }
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(10, monitor.getThroughput());
        
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(5, monitor.getThroughput());
        
        monitor.onEvent(scenarioRunnerEventFactory.end());
        
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(5, monitor.getThroughput());
    }
    
}
