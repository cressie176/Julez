package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.event.ScenarioEventHandler;
import uk.co.acuminous.julez.scenario.event.ThroughputMonitor;
import uk.co.acuminous.julez.test.TestUtils;

public class MultiScenarioPerformanceTest {

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        ScenarioRunner runner1 = prepareForTestRun(new HelloWorldScenario(), 100, combinedMonitor, monitor1);
        ScenarioRunner runner2 = prepareForTestRun(new GoodbyeWorldScenario(), 50, combinedMonitor, monitor2);        

        new MultiConcurrentScenarioRunner(runner1, runner2).run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    private ScenarioRunner prepareForTestRun(Scenario scenario, int size, ScenarioEventHandler... listeners) {
        scenario.registerListeners(listeners);        
        Scenarios scenarios = TestUtils.getScenarios(scenario, size);
        return new ConcurrentScenarioRunner().queue(scenarios);
    }

    class HelloWorldScenario extends BaseScenario {
        public void run() {
            start();
            System.out.print("Hello World ");
            pass();
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        public void run() {
            start();
            System.out.print("Goodbye World ");
            pass();
        }
    }
}
