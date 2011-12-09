package examples.basics;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.PassFailErrorScenario;

public class ScenarioOutcomeTest {

    @Test
    public void demonstrateRecordingConcurrentScenarioResults() {
        
        PassFailErrorScenario scenario = new PassFailErrorScenario();
        
        ResultMonitor resultMonitor = new ResultMonitor();
        scenario.register(resultMonitor);        
                        
        ScenarioSource scenarios = new SizeLimiter().restrict(new ScenarioRepeater(scenario)).applyAt(200);
        
        new ConcurrentScenarioRunner().queue(scenarios).allocate(10, THREADS).go();

        assertEquals(129, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(21, resultMonitor.getErrorCount());
        assertEquals(64, resultMonitor.getPercentage());
    }


}
