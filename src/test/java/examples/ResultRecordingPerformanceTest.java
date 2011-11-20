package examples;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class ResultRecordingPerformanceTest {

    @Test
    public void demonstrateRecordingScenarioResults() {
        
        String correlationId = UUID.randomUUID().toString();
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationId);
        
        ResultRecordingScenario scenario = new ResultRecordingScenario(scenarioEventFactory);
        
        ResultMonitor resultMonitor = new ResultMonitor();
        scenario.registerEventHandler(resultMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 200);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).queue(scenarios);
        runner.run();

        assertEquals(150, resultMonitor.getPassCount());
        assertEquals(50, resultMonitor.getFailureCount());
        assertEquals(75, resultMonitor.getPercentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private final AtomicInteger counter = new AtomicInteger();

        public ResultRecordingScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }

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
