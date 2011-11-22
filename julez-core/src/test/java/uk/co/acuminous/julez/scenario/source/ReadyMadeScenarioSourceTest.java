package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;

public class ReadyMadeScenarioSourceTest {

    @Test
    public void returnsScenariosFromTheUnderlyingList() {
       
        ReadyMadeScenarioSource scenarios = new ReadyMadeScenarioSource(new NoOpScenario(), new NoOpScenario(), new NoOpScenario());
        
        assertEquals(3, scenarios.available());
        assertNotNull(scenarios.next());
        
        assertEquals(2, scenarios.available());
        assertNotNull(scenarios.next());

        assertEquals(1, scenarios.available());
        assertNotNull(scenarios.next());

        assertEquals(0, scenarios.available());
        assertNull(scenarios.next());

        assertEquals(0, scenarios.available());
        assertNull(scenarios.next());        
    }
    
}
