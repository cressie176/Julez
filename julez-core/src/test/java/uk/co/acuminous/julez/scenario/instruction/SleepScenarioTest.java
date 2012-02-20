package uk.co.acuminous.julez.scenario.instruction;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.instruction.SleepScenario;


public class SleepScenarioTest {

    @Test
    public void scenarioSleepsForTheRequestedTime() {

        long before = System.currentTimeMillis();
        
        new SleepScenario().sleepFor(300, MILLISECONDS).run();
        
        long duration = System.currentTimeMillis() - before;
        
        assertTrue(duration >= 300);
        assertTrue(duration < 500);
    }
    
    @Test
    public void scenarioSleepsFor1SecondByDefault() {

        long before = System.currentTimeMillis();
        
        new SleepScenario().run();
        
        long duration = System.currentTimeMillis() - before;
        
        assertTrue(duration >= 1000);
        assertTrue(duration < 1200);        
    }    
    
}
