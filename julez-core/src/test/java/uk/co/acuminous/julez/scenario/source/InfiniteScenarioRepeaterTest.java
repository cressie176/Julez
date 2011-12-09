package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.NoOpScenario;

public class InfiniteScenarioRepeaterTest {

    @Test
    public void repeatsTheGivenScenarioForInfinity() {
        
        Scenario scenario = new NoOpScenario();
        InfiniteScenarioRepeater scenarios = new InfiniteScenarioRepeater(scenario);
        
        for (int i = 0; i < 1000; i++) {
            assertSame(scenario, scenarios.next());    
        }        
    }
    
    
}
