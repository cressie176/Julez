package uk.co.acuminous.julez.scenario.instruction;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.instruction.StopScenarioRunnerScenario;

public class ScenarioRunnerStarterTest {

    @Test
    public void terminatesScenarioRunner() {
        DummyScenarioRunner runner = new DummyScenarioRunner();
        
        new StopScenarioRunnerScenario(runner).run();
        
        assertTrue(runner.stopped);
    }
}
