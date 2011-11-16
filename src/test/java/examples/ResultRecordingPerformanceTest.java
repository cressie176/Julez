package examples;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.event.ResultMonitor;
import uk.co.acuminous.julez.test.TestUtils;

public class ResultRecordingPerformanceTest {

    @Test
    public void demonstrateRecordingScenarioResults() {
        
        ResultRecordingScenario scenario = new ResultRecordingScenario();
        
        ResultMonitor resultMonitor = new ResultMonitor();
        scenario.registerListeners(resultMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 200);
        
        new ConcurrentScenarioRunner().queue(scenarios).run();

        assertEquals(150, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(75, resultMonitor.getPercentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private AtomicInteger counter = new AtomicInteger();

        public void run() {
            start();
            if (counter.incrementAndGet() % 4 == 0) {
                fail();
            } else {
                pass();
            }
        }
    }

}
