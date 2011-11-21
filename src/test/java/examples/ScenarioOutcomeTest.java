package examples;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioSource;
import uk.co.acuminous.julez.test.TestUtils;

public class ScenarioOutcomeTest {

    @Test
    public void demonstrateRecordingConcurrentScenarioResults() {
        
        final ResultRecordingScenario scenario = new ResultRecordingScenario();
        
        final ResultMonitor resultMonitor = new ResultMonitor();
        scenario.registerEventHandler(resultMonitor);        
        
        final ScenarioSource scenarios = TestUtils.getScenarios(scenario, 200);
        
        final ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.run();

        assertEquals(150, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(75, resultMonitor.getPercentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private final AtomicInteger counter = new AtomicInteger();

        public void run() {
            raise(eventFactory.begin());
            if (counter.incrementAndGet() % 4 == 0) {
                raise(eventFactory.fail());
            } else {
                raise(eventFactory.pass());
            }
        }
    }

}
