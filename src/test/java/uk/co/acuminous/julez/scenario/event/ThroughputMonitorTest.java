package uk.co.acuminous.julez.scenario.event;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ThroughputMonitorTest {

    @Test
    public void calculatesThroughputFromPasses() {
        assertThroughput(ScenarioEvent.pass());
    }
    
    @Test
    public void calculatesThroughputFromFailures() {
        assertThroughput(ScenarioEvent.fail());
    }
    
    @Test
    public void calculatesThroughputForVeryQuickScenarios() {
        ThroughputMonitor monitor = new ThroughputMonitor();
        
        monitor.onEvent(ScenarioRunnerEvent.begin());
        monitor.onEvent(ScenarioEvent.begin());
        monitor.onEvent(ScenarioEvent.pass());
        monitor.onEvent(ScenarioRunnerEvent.end());
        
        assertFalse("Throughput was not calculated for extremely quick scenario", 0 == monitor.getThroughput());
    }  
    
    private void assertThroughput(ScenarioEvent event) {
        ThroughputMonitor monitor = new ThroughputMonitor();
        
        monitor.onEvent(ScenarioRunnerEvent.begin());
        
        for (int i = 0; i < 10; i++) {
            monitor.onEvent(ScenarioEvent.begin());            
            monitor.onEvent(event);
        }
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(10, monitor.getThroughput());
        
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(5, monitor.getThroughput());
        
        monitor.onEvent(ScenarioRunnerEvent.end());
        
        ConcurrencyUtils.sleep(1, SECONDS);
        
        assertEquals(5, monitor.getThroughput());
    }
    
}
