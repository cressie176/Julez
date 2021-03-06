package uk.co.acuminous.julez.event.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class ResultMonitorTest {

    private ScenarioEventFactory scenarioEventFactory;

    @Before
    public void init() {        
        scenarioEventFactory = new ScenarioEventFactory();        
    }
    
    @Test
    public void countsPasses() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPassCount());
        
        resultMonitor.onEvent(scenarioEventFactory.pass());
        assertEquals(1, resultMonitor.getPassCount());
        
        resultMonitor.onEvent(scenarioEventFactory.fail());
        resultMonitor.onEvent(scenarioEventFactory.error());        
        assertEquals(1, resultMonitor.getPassCount());        
    }
    
    @Test
    public void countsFailures() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getFailureCount());
        
        resultMonitor.onEvent(scenarioEventFactory.fail());
        assertEquals(1, resultMonitor.getFailureCount());
        
        resultMonitor.onEvent(scenarioEventFactory.pass());
        resultMonitor.onEvent(scenarioEventFactory.error());                
        assertEquals(1, resultMonitor.getFailureCount());        
    }    
    
    @Test
    public void countsErrors() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getErrorCount());
        
        resultMonitor.onEvent(scenarioEventFactory.error());
        assertEquals(1, resultMonitor.getErrorCount());
        
        resultMonitor.onEvent(scenarioEventFactory.pass());
        resultMonitor.onEvent(scenarioEventFactory.fail());                
        assertEquals(1, resultMonitor.getErrorCount());        
    }     
    
    @Test
    public void calculatesPercentage() {       
        ResultMonitor resultMonitor = new ResultMonitor();
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onEvent(scenarioEventFactory.fail());
        assertEquals(0, resultMonitor.getPercentage());
        
        resultMonitor.onEvent(scenarioEventFactory.pass());
        assertEquals(50, resultMonitor.getPercentage()); 
        
        resultMonitor.onEvent(scenarioEventFactory.fail());
        resultMonitor.onEvent(scenarioEventFactory.fail());        
        assertEquals(25, resultMonitor.getPercentage());
        
        resultMonitor.onEvent(scenarioEventFactory.error());
        assertEquals(20, resultMonitor.getPercentage());
    }       
}
