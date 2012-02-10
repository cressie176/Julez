package uk.co.acuminous.julez.scenario.control;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ScenarioRunnerStarterTest {

    @Test
    public void terminatesScenarioRunner() {
        DummyScenarioRunner runner = new DummyScenarioRunner();
        
        new ScenarioRunnerTerminator(runner).run();
        
        assertTrue(runner.stopped);
    }
}
