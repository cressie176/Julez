package examples;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class ScenarioOutcomeTest {

    @Test
    public void demonstrateRecordingConcurrentScenarioResults() {
        
        final ResultRecordingScenario scenario = new ResultRecordingScenario();
        
        final ResultMonitor resultMonitor = new ResultMonitor();
        scenario.register(resultMonitor);        
        
        final ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 200);
        
        new ConcurrentScenarioRunner().queue(scenarios).allocate(10, THREADS).go();

        assertEquals(150, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(75, resultMonitor.getPercentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private final AtomicInteger counter = new AtomicInteger();

        public void run() {
            onEvent(eventFactory.begin());
            if (counter.incrementAndGet() % 4 == 0) {
                onEvent(eventFactory.fail());
            } else {
                onEvent(eventFactory.pass());
            }
            onEvent(eventFactory.end());
        }
    }

}
