package uk.co.acuminous.julez.scenario;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResultMonitorTest {

    @Test
    public void countsPasses() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPassCount());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.pass());
        assertEquals(1, resultMonitor.getPassCount());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.fail());
        assertEquals(1, resultMonitor.getPassCount());        
    }
    
    @Test
    public void countsFailures() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getFailureCount());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.fail());
        assertEquals(1, resultMonitor.getFailureCount());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.pass());
        assertEquals(1, resultMonitor.getFailureCount());        
    }    
    
    
    @Test
    public void calculatesPercentage() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.fail());
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onScenarioEvent(ScenarioEvent.pass());
        assertEquals(50, resultMonitor.getPercentage()); 
        
        resultMonitor.onScenarioEvent(ScenarioEvent.fail());
        resultMonitor.onScenarioEvent(ScenarioEvent.fail());        
        assertEquals(25, resultMonitor.getPercentage());
    }       
}
