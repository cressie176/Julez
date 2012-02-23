package uk.co.acuminous.julez.scenario.limiter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.limiter.DurationLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.util.ConcurrencyUtils;


public class DurationLimiterTest {

    @Test
    public void sourceOffersScenariosBeforeTimeoutHasNotBeenReached() {        
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        DurationLimiter timedScenarioSource = new DurationLimiter(scenarios, 1, SECONDS);
        
        for (int i = 0; i < 3; i++) {
            assertNotNull("Source returned null", timedScenarioSource.next());
        }
    }
    
    @Test
    public void sourceReturnsNullOnOrAfterTimeout() {
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        DurationLimiter timedScenarioSource = new DurationLimiter(scenarios, 0, MILLISECONDS);
                
        assertNull(timedScenarioSource.next());
    }
    
    @Test
    public void timeoutStartsFromFirstCallToNext() {
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        DurationLimiter timedScenarioSource = new DurationLimiter(scenarios, 100, MILLISECONDS);
        
        ConcurrencyUtils.sleep(200, MILLISECONDS);
        
        assertNotNull("Source shutdown before the timeout", timedScenarioSource.next());
        ConcurrencyUtils.sleep(100, MILLISECONDS);        
        assertNull("Source did not shutdown after the timeout", timedScenarioSource.next());
    } 
    
    @Test
    public void testUsingFluidApi() {
        ScenarioSource scenarios = new ScenarioRepeater(new NoOpScenario());
        DurationLimiter timedScenarioSource = new DurationLimiter().limit(scenarios).to(100, MILLISECONDS);
        
        ConcurrencyUtils.sleep(200, MILLISECONDS);
        
        assertNotNull("Source shutdown before the timeout", timedScenarioSource.next());
        ConcurrencyUtils.sleep(100, MILLISECONDS);        
        assertNull("Source did not shutdown after the timeout", timedScenarioSource.next());
        
    }
}
