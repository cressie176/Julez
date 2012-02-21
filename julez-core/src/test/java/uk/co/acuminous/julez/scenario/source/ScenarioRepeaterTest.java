package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import static uk.co.acuminous.julez.util.JulezSugar.*;

public class ScenarioRepeaterTest {

    @Test
    public void repeatsTheGivenScenarioForInfinity() {
        Scenario scenario = new NoOpScenario();
        ScenarioRepeater scenarios = new ScenarioRepeater(scenario);

        for (int i = 0; i < 1000; i++) {
            assertSame(scenario, scenarios.next());
        }
    }
    
    @Test
    public void repeatsTheGivenScenarioForTheSpecifiedNumberOfTimes() {
        Scenario scenario = new NoOpScenario();
        ScenarioRepeater scenarios = new ScenarioRepeater(scenario, 10);

        for (int i = 0; i < 10; i++) {
            assertSame(scenario, scenarios.next());
        }
        
        assertNull("Scenario was repeated more than 10 times", scenarios.next());        
    }    
    
    @Test
    public void testFluidApi() {
        
        Scenario scenario = new NoOpScenario();
        ScenarioRepeater scenarios = new ScenarioRepeater().repeat(scenario).atMost(10, TIMES);
        
        for (int i = 0; i < 10; i++) {
            assertSame(scenario, scenarios.next());
        }
        
        assertNull("Scenario was repeated more than 10 times", scenarios.next());
    }
}
