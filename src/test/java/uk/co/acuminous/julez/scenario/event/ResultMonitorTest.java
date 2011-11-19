package uk.co.acuminous.julez.scenario.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ResultMonitor;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class ResultMonitorTest {

    @Test
    public void countsPasses() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPassCount());
        
        resultMonitor.onEvent(ScenarioEvent.pass());
        assertEquals(1, resultMonitor.getPassCount());
        
        resultMonitor.onEvent(ScenarioEvent.fail());
        assertEquals(1, resultMonitor.getPassCount());        
    }
    
    @Test
    public void countsFailures() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getFailureCount());
        
        resultMonitor.onEvent(ScenarioEvent.fail());
        assertEquals(1, resultMonitor.getFailureCount());
        
        resultMonitor.onEvent(ScenarioEvent.pass());
        assertEquals(1, resultMonitor.getFailureCount());        
    }    
    
    
    @Test
    public void calculatesPercentage() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onEvent(ScenarioEvent.fail());
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onEvent(ScenarioEvent.pass());
        assertEquals(50, resultMonitor.getPercentage()); 
        
        resultMonitor.onEvent(ScenarioEvent.fail());
        resultMonitor.onEvent(ScenarioEvent.fail());        
        assertEquals(25, resultMonitor.getPercentage());
    }       
}
