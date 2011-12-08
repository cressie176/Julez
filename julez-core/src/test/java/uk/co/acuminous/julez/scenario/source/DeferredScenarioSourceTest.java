package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.NoOpScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;

public class DeferredScenarioSourceTest {

    @Test
    public void blocksUntilStartTime() {
        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);

        DateTime before = new DateTime();

        DeferredScenarioSource delayedScenarios = new DeferredScenarioSource(scenarios, before.plusSeconds(2).getMillis());
        delayedScenarios.next();
        DateTime after = new DateTime();

        Duration delay = new Duration(before, after);
        System.out.println(delay);

        assertTrue("Scenario queue was not delayed", delay.getStandardSeconds() >= 2);
    }

    @Test
    public void returnsUnderlyingQueueSize() {

        ScenarioSource scenarios = new SizedScenarioRepeater(new NoOpScenario(), 100);
        DeferredScenarioSource delayedScenarios = new DeferredScenarioSource(scenarios, new DateTime().plusSeconds(1).getMillis());

        assertEquals(100, delayedScenarios.available());

        delayedScenarios.next();

        assertEquals(99, delayedScenarios.available());
    }
}
