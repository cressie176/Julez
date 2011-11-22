package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.Scenario;

public class InfiniteScenarioRepeaterTest {

    @Test
    public void repeatsTheGivenScenarioForInfinity() {
        
        Scenario scenario = new NoOpScenario();
        InfiniteScenarioRepeater scenarios = new InfiniteScenarioRepeater(scenario);
        
        assertEquals(Integer.MAX_VALUE, scenarios.available());
        assertEquals(scenario, scenarios.next());
        
        assertEquals(Integer.MAX_VALUE, scenarios.available());
        assertEquals(scenario, scenarios.next());
        
    }
    
    
}
