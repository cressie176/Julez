package uk.co.acuminous.julez.scenario.control;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class SleepingScenarioTest {

    @Test
    public void scenarioSleepsForTheRequestedTime() {

        long before = System.currentTimeMillis();
        
        new SleepingScenario().sleepFor(300, MILLISECONDS).run();
        
        long duration = System.currentTimeMillis() - before;
        
        assertTrue(duration >= 300);
        assertTrue(duration < 500);
    }
    
    @Test
    public void scenarioSleepsFor1SecondByDefault() {

        long before = System.currentTimeMillis();
        
        new SleepingScenario().run();
        
        long duration = System.currentTimeMillis() - before;
        
        assertTrue(duration >= 1000);
        assertTrue(duration < 1200);        
    }    
    
}
