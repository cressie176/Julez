package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.ThroughputMonitor;
import uk.co.acuminous.julez.test.TestUtils;

public class MultiScenarioPerformanceTest {

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        ScenarioRunner runner1 = getScenarioRunner(new HelloWorldScenario(), 100, combinedMonitor, monitor1);
        ScenarioRunner runner2 = getScenarioRunner(new GoodbyeWorldScenario(), 50, combinedMonitor, monitor2);        

        new MultiConcurrentScenarioRunner(runner1, runner2).run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    private ScenarioRunner getScenarioRunner(Scenario scenario, int size, ThroughputMonitor combinedMonitor, ThroughputMonitor monitor1) {
        scenario.registerListeners(combinedMonitor, monitor1);        
        Scenarios helloWorldScenarios = TestUtils.getScenarios(scenario, size);
        ScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios);
        return runner1;
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
