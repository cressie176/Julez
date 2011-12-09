package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.test.NoOpScenario;

public class ScenarioHopperTest {

    @Test
    public void returnsScenariosFromTheUnderlyingList() {
       
        NoOpScenario scenario1 = new NoOpScenario();
        NoOpScenario scenario2 = new NoOpScenario();
        NoOpScenario scenario3 = new NoOpScenario();
        
        ScenarioHopper scenarios = new ScenarioHopper(scenario1, scenario2, scenario3);
        
        assertSame(scenario1, scenarios.next());
        assertSame(scenario2, scenarios.next());
        assertSame(scenario3, scenarios.next());
        
        assertNull(scenarios.next());
    }
    
}
