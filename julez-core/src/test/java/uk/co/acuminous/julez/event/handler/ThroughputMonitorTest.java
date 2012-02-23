package uk.co.acuminous.julez.event.handler;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ThroughputMonitorTest {

    private ScenarioEventFactory scenarioEventFactory;
    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    private ScenarioThroughputMonitor monitor;
    private DateTime timestamp;

    @Before
    public void init() {
        scenarioEventFactory = new ScenarioEventFactory();
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();
        monitor = new ScenarioThroughputMonitor();
        timestamp = new DateTime();        
    }

    @Test
    public void tolleratesThroughputEnquiriesBeforeTheScenarioRunnerHasStarted() {
        assertTrue(0 == new ScenarioThroughputMonitor().getThroughput());        
    }
    
    public void disregardsScenarioEventsBeforeArrivalOfScenarioRunnerBeginEvent() {
        scenario();
        assertTrue(wasDisregarded());
    }
    
    @Test
    public void regardsScenarioEventsArrivingAfterScenarioRunnerBeginEventWithIdenticalTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenario(timestamp);               
        assertFalse(wasDisregarded());
    }
    
    @Test
    public void regardsScenarioEventsArrivingAfterScenarioRunnerBeginEventWithAscendingTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenario(timestamp.plusMillis(1));               
        assertFalse(wasDisregarded());
    }
    
    @Test
    public void disregardsScenarioEventsArrivingAfterScenarioRunnerBeginEventWithDescendingTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenario(timestamp.minusMillis(1));               
        assertTrue(wasDisregarded());
    }
     
    @Test
    public void regardsScenarioEventsArrivingBetweenScenarioRunnerBeginAndEndEventsWithIdenticalTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenario(timestamp);
        scenarioRunnerEnd(timestamp);
        assertFalse(wasDisregarded());
    }
    
    @Test
    public void regardsScenarioEventsArrivingBetweenScenarioRunnerBeginAndEndEventsWithAscendingTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenario(timestamp.plusMillis(1));
        scenarioRunnerEnd(timestamp.plusMillis(2));
        assertFalse(wasDisregarded());
    }    

    @Test
    public void disregardsScenarioEventsArrivingAfterBothScenarioRunnerBeginAndEndEventsWithAscendingTimestamps() {
        scenarioRunnerBegin(timestamp);
        scenarioRunnerEnd(timestamp.plusMillis(1));
        scenario(timestamp.plusMillis(2));        
        assertTrue(wasDisregarded());
    }    

    @Test
    public void regardsScenarioEventsArrivingAfterBothScenarioRunnerBeginAndEndEventsWithTimestampIdenticalToScenarioRunnerBeginEvent() {
        scenarioRunnerBegin(timestamp);
        scenarioRunnerEnd(timestamp.plusMillis(1));
        scenario(timestamp);        
        assertFalse(wasDisregarded());
    }
    
    @Test
    public void regardsScenarioEventsArrivingAfterBothScenarioRunnerBeginAndEndEventsWithTimestampIdenticalToScenarioRunnerEndEvent() {
        scenarioRunnerBegin(timestamp);
        scenarioRunnerEnd(timestamp.plusMillis(1));
        scenario(timestamp.plusMillis(1));        
        assertFalse(wasDisregarded());
    }

    @Test
    public void regardsScenarioEventsArrivingAfterBothScenarioRunnerBeginAndEndEventsWithTimestampBetweenScenarioRunnerEvents() {
        scenarioRunnerBegin(timestamp);
        scenarioRunnerEnd(timestamp.plusMillis(2));
        scenario(timestamp.plusMillis(1));        
        assertFalse(wasDisregarded());
    }
    
    
    @Test
    public void disregardsScenarioEventsArrivingAfterScenarioRunnerBeginButButStampedAsBefore() {

        ScenarioThroughputMonitor monitor = new ScenarioThroughputMonitor();
        
        DateTime now = new DateTime();
        
        monitor.onEvent(new ScenarioRunnerEvent("id", now.getMillis(), ScenarioRunnerEvent.BEGIN));        
        monitor.onEvent(new ScenarioEvent("id", now.minusMillis(1).getMillis(), ScenarioEvent.END));
        
        assertEquals(0, monitor.getThroughput());
    }
    
    @Test
    public void calculatesThroughputWhileScenarioRunnerIsRunning() {
                
        scenarioRunnerBegin();
        
        for (int i = 0; i < 10; i++) {
            scenario();
        }
        
        ConcurrencyUtils.sleep(500, MILLISECONDS);
        
        assertEquals(20, monitor.getThroughput());
        
        ConcurrencyUtils.sleep(500, MILLISECONDS);
        
        assertEquals(10, monitor.getThroughput());
        
        scenarioRunnerEnd();
    }
    
    @Test
    public void regurgitatesThroughputAfterScenarioRunnerIsFinished() {
        ScenarioThroughputMonitor monitor = new ScenarioThroughputMonitor();
                
        scenarioRunnerBegin();
        
        for (int i = 0; i < 10; i++) {
            scenario();
        }
        
        int throughput = monitor.getThroughput();
        
        scenarioRunnerEnd();
        
        ConcurrencyUtils.sleep(100, MILLISECONDS);
        
        assertEquals(throughput, monitor.getThroughput());
    }    
    
    @Test
    public void calculatesThroughputForVeryQuickScenarios() {
        ScenarioThroughputMonitor monitor = new ScenarioThroughputMonitor();
        
        monitor.onEvent(scenarioRunnerEventFactory.begin());
        monitor.onEvent(scenarioEventFactory.begin());
        monitor.onEvent(scenarioEventFactory.pass());
        monitor.onEvent(scenarioEventFactory.end());
        monitor.onEvent(scenarioRunnerEventFactory.end());
        
        assertFalse("Throughput was not calculated for extremely quick scenario", 0 == monitor.getThroughput());
    }
    
    public void scenario() {
        scenario(new DateTime());                
    }    
    
    public void scenario(DateTime timestamp) {
        monitor.onEvent(new ScenarioEvent(UUID.randomUUID().toString(), timestamp.getMillis(), ScenarioEvent.BEGIN));
        monitor.onEvent(new ScenarioEvent(UUID.randomUUID().toString(), timestamp.getMillis(), ScenarioEvent.END));
    }
    
    public void scenarioRunnerBegin() {
        scenarioRunnerBegin(new DateTime());                
    }
    
    private void scenarioRunnerBegin(DateTime timestamp) {
        monitor.onEvent(new ScenarioRunnerEvent(UUID.randomUUID().toString(), timestamp.getMillis(), ScenarioRunnerEvent.BEGIN));       
    }

    public void scenarioRunnerEnd() {
        scenarioRunnerEnd(new DateTime());                
    }    
    
    private void scenarioRunnerEnd(DateTime timestamp) {
        monitor.onEvent(new ScenarioRunnerEvent(UUID.randomUUID().toString(), timestamp.getMillis(), ScenarioRunnerEvent.END));       
    }
    
    private boolean wasDisregarded() {
        return (0 == monitor.getThroughput());        
    }
}
