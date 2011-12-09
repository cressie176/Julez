package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.test.NoOpScenario;

public class ReadyMadeScenarioSourceTest {

    @Test
    public void returnsScenariosFromTheUnderlyingList() {
       
        PrePopulatedScenarioSource scenarios = new PrePopulatedScenarioSource(new NoOpScenario(), new NoOpScenario(), new NoOpScenario());
        
        assertNotNull(scenarios.next());
        assertNotNull(scenarios.next());
        assertNotNull(scenarios.next());
        assertNull(scenarios.next());
    }
    
}
