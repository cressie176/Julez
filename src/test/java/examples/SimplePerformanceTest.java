package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.util.UUID;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class SimplePerformanceTest {

    @Test
    public void demonstrateASimplePerformanceTest() {

        String correlationId = UUID.randomUUID().toString();
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationId);
        
        HelloWorldScenario scenario = new HelloWorldScenario(scenarioEventFactory);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);                        

        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).queue(scenarios);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

        assertMinimumThroughput(500, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public HelloWorldScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }

        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.pass());
        }
    }
}
