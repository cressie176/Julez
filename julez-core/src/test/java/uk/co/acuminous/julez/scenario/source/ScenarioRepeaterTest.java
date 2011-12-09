package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.test.NoOpScenario;

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
    public void repeatsTheGivenScenarioForALimitedNumberOfTimes() {
        Scenario scenario = new NoOpScenario();
        ScenarioSource scenarios = new ScenarioRepeater(scenario).repeat(3);

        assertSame(scenario, scenarios.next());
        assertSame(scenario, scenarios.next());
        assertSame(scenario, scenarios.next());
        
        assertNull(scenarios.next());
    }

}
