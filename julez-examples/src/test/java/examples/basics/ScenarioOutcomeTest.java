package examples.basics;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.PassFailErrorScenario;


public class ScenarioOutcomeTest {

    @Test
    public void demonstrateRecordingConcurrentScenarioResults() {

        ScenarioResultMonitor resultMonitor = new ScenarioResultMonitor();        
        
        Scenario scenario = new PassFailErrorScenario().register(resultMonitor);        
                        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).atMost(200, TIMES);
        
        new ConcurrentScenarioRunner().allocate(10, THREADS).queue(scenarios).start();

        assertEquals(129, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(21, resultMonitor.getErrorCount());
        assertEquals(64, resultMonitor.getPercentage());
    }
}
