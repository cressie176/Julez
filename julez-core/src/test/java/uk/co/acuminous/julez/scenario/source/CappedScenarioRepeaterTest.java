package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.NoOpScenario;

public class CappedScenarioRepeaterTest {

    @Test
    public void repeatsTheGivenScenarioForTheSpecifiedNumberOfTimes() {
        
        Scenario scenario = new NoOpScenario();
        SizedScenarioRepeater scenarios = new SizedScenarioRepeater(scenario, 3);
        
        assertEquals(scenario, scenarios.next());
        assertEquals(scenario, scenarios.next());
        assertEquals(scenario, scenarios.next());
        assertNull(scenarios.next());
    }
    
    
}
