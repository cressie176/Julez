package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.Scenario;

public class CappedScenarioRepeaterTest {

    @Test
    public void repeatsTheGivenScenarioForTheSpecifiedNumberOfTimes() {
        
        Scenario scenario = new NoOpScenario();
        CappedScenarioRepeater scenarios = new CappedScenarioRepeater(scenario, 3);
        
        assertEquals(3, scenarios.available());
        assertEquals(scenario, scenarios.next());
        
        assertEquals(2, scenarios.available());
        assertEquals(scenario, scenarios.next());
        
        assertEquals(1, scenarios.available());
        assertEquals(scenario, scenarios.next());
        
        assertEquals(0, scenarios.available());
        assertNull(scenarios.next());
        
        assertEquals(0, scenarios.available());
        assertNull(scenarios.next());
    }
    
    
}
