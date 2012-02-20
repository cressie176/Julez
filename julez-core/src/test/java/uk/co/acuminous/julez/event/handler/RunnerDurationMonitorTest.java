package uk.co.acuminous.julez.event.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import static java.util.concurrent.TimeUnit.*;

import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class RunnerDurationMonitorTest {

    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    
    @Before
    public void init() {
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();                
    }

    @Test
    public void reportsDurationBeforeStarting() {
        assertEquals(0, new ScenarioRunnerDurationMonitor().getDuration());
    }
    
    @Test
    public void reportsDurationWhileRunning() {        
        ScenarioRunnerDurationMonitor monitor = new ScenarioRunnerDurationMonitor();
        monitor.onEvent(scenarioRunnerEventFactory.begin());        
        
        ConcurrencyUtils.sleep(250, MILLISECONDS);
        long duration = monitor.getDuration();
        assertTrue("Incorrect duration", duration >= 250);
        assertTrue("Incorrect duration", monitor.getDuration() < 500);
        
        ConcurrencyUtils.sleep(250, MILLISECONDS);
        duration = monitor.getDuration();
        assertTrue("Incorrect duration", duration >= 500);
        assertTrue("Incorrect duration", monitor.getDuration() < 750);        
    }
    
    @Test
    public void reportsDurationWhenFinished() {
        
        ScenarioRunnerDurationMonitor monitor = new ScenarioRunnerDurationMonitor();

        monitor.onEvent(scenarioRunnerEventFactory.begin());                
        
        ConcurrencyUtils.sleep(250, MILLISECONDS);        
        
        monitor.onEvent(scenarioRunnerEventFactory.end());
        
        long duration = monitor.getDuration();

        ConcurrencyUtils.sleep(250, MILLISECONDS);
        
        assertEquals(duration, monitor.getDuration());        
    }
    
}
