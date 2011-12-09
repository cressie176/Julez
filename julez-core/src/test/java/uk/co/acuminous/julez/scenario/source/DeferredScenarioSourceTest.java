package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.test.NoOpScenario;

public class DeferredScenarioSourceTest {

    @Test
    public void blocksUntilStartTime() {
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);

        DateTime before = new DateTime();

        DeferredScenarioSource delayedScenarios = new DeferredScenarioSource(scenarios, before.plusSeconds(2).getMillis());
        delayedScenarios.next();
        
        Duration delay = new Duration(before, new DateTime());
        
        assertTrue("Scenario queue was not delayed", delay.getStandardSeconds() >= 2);
    }
}
