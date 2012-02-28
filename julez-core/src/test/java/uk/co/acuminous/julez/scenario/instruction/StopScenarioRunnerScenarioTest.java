package uk.co.acuminous.julez.scenario.instruction;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.instruction.ScenarioRunnerStarter;

public class StopScenarioRunnerScenarioTest {

    @Test
    public void terminatesScenarioRunner() {
        DummyScenarioRunner runner = new DummyScenarioRunner();
        
        new ScenarioRunnerStarter(runner).run();
        
        assertTrue(runner.started);
    }
}