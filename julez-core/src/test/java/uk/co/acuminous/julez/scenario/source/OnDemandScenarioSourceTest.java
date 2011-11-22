package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.Scenario;

public class OnDemandScenarioSourceTest {

    @Test
    public void createsScenariosOnDemand() {
        OnDemandScenarioSource scenarios = new OnDemandScenarioSource() {
            @Override public Scenario next() {
                return new NoOpScenario();
            }            
        };
        
        assertEquals(Integer.MAX_VALUE, scenarios.available());
        Scenario scenario1 = scenarios.next();
        assertTrue(scenario1 instanceof NoOpScenario);
        
        assertEquals(Integer.MAX_VALUE, scenarios.available());
        Scenario scenario2 = scenarios.next();        
        assertTrue(scenario2 instanceof NoOpScenario);
        
        assertTrue(scenario1 != scenario2);
    }
    
}
