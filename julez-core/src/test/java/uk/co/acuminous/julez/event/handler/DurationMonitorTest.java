package uk.co.acuminous.julez.event.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import static java.util.concurrent.TimeUnit.*;

import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class DurationMonitorTest {

    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    
    @Before
    public void init() {
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();                
    }

    @Test
    public void reportsDurationBeforeStarting() {
        assertEquals(0, new DurationMonitor().getDuration());
    }
    
    @Test
    public void reportsDurationWhileRunning() {        
        DurationMonitor monitor = new DurationMonitor();
        monitor.onEvent(scenarioRunnerEventFactory.begin());        
        
        ConcurrencyUtils.sleep(500, MILLISECONDS);
        long duration = monitor.getDuration();
        assertTrue("Incorrect duration", duration >= 500);
        assertTrue("Incorrect duration", monitor.getDuration() < 1000);
        
        ConcurrencyUtils.sleep(500, MILLISECONDS);
        duration = monitor.getDuration();
        assertTrue("Incorrect duration", duration >= 1000);
        assertTrue("Incorrect duration", monitor.getDuration() < 1500);        
    }
    
    @Test
    public void reportsDurationWhenFinished() {
        
        DurationMonitor monitor = new DurationMonitor();
        monitor.onEvent(scenarioRunnerEventFactory.begin());        
        
        ConcurrencyUtils.sleep(500, MILLISECONDS);
        
        monitor.onEvent(scenarioRunnerEventFactory.end());
        
        long duration = monitor.getDuration();

        ConcurrencyUtils.sleep(500, MILLISECONDS);
        
        assertEquals(duration, monitor.getDuration());        
    }
    
}
