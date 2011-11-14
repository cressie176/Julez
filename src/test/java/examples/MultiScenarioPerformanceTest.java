package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class MultiScenarioPerformanceTest {

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        Scenarios helloWorldScenarios = TestUtils.getScenarios(new HelloWorldScenario(), 100);
        ScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios);

        Scenarios goodbyeWorldScenarios = TestUtils.getScenarios(new GoodbyeWorldScenario(), 100);
        ScenarioRunner runner2 = new ConcurrentScenarioRunner().queue(goodbyeWorldScenarios);

        ScenarioRunner multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
        multiRunner.run();

        assertMinimumThroughput(500, runner1.throughput());
        assertMinimumThroughput(500, runner2.throughput());
        assertMinimumThroughput(1000, multiRunner.throughput());
    }

    class HelloWorldScenario extends BaseScenario {
        public void run() {
            System.out.print("Hello World ");
            notifyComplete();
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        public void run() {
            System.out.print("Goodbye World ");
            notifyComplete();
        }
    }
}
