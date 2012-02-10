package uk.co.acuminous.julez.scenario.control;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScenarioRunnerTerminatorTest {

    @Test
    public void terminatesScenarioRunner() {
        DummyScenarioRunner runner = new DummyScenarioRunner();
        
        new ScenarioRunnerStarter(runner).run();
        
        assertTrue(runner.started);
    }
}
