package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.util.UUID;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class MultiScenarioPerformanceTest {

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        String correlationId = UUID.randomUUID().toString();
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationId);
        
        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        HelloWorldScenario helloWorldScenario = new HelloWorldScenario(scenarioEventFactory);
        helloWorldScenario.registerEventHandler(monitor1, combinedMonitor);
        
        Scenarios helloWorldScenarios = TestUtils.getScenarios(helloWorldScenario, 100);
        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).queue(helloWorldScenarios);
        runner1.registerEventHandler(monitor1);
        
        GoodbyeWorldScenario goodbyeWorldScenario = new GoodbyeWorldScenario(scenarioEventFactory);
        goodbyeWorldScenario.registerEventHandler(monitor2, combinedMonitor);
        
        Scenarios goodbyeWorldScenarios = TestUtils.getScenarios(goodbyeWorldScenario, 100);
        ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).queue(goodbyeWorldScenarios);
        runner2.registerEventHandler(monitor2);

        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(scenarioRunnerEventFactory, runner1, runner2);
        runner.registerEventHandler(combinedMonitor);
        runner.run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
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

    class GoodbyeWorldScenario extends BaseScenario {
        
        public GoodbyeWorldScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }

        public void run() {
            raise(eventFactory.begin());
            System.out.print("Goodbye World ");
            raise(eventFactory.pass());
        }
    }
}
