package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class MultiScenarioPerformanceTest {

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        ConcurrentScenarioRunner runner1 = prepareForTestRun(new HelloWorldScenario(), 100, combinedMonitor, monitor1);
        runner1.registerEventHandler(monitor1);
        
        ConcurrentScenarioRunner runner2 = prepareForTestRun(new GoodbyeWorldScenario(), 50, combinedMonitor, monitor2);        
        runner2.registerEventHandler(monitor2);
        
        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(runner1, runner2);
        runner.registerEventHandler(combinedMonitor);
        runner.run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    private ConcurrentScenarioRunner prepareForTestRun(Scenario scenario, int size, EventHandler... handlers) {
        scenario.registerEventHandler(handlers);        
        Scenarios scenarios = TestUtils.getScenarios(scenario, size);
        return new ConcurrentScenarioRunner().queue(scenarios);
    }

    class HelloWorldScenario extends BaseScenario {
        public void run() {
            begin();
            System.out.print("Hello World ");
            pass();
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        public void run() {
            begin();
            System.out.print("Goodbye World ");
            pass();
        }
    }
}
