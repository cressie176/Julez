package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.util.Deque;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.TestUtils;

public class SimplePerformanceTest {

    @Test
    public void demonstrateASimplePerformanceTest() {

    	Deque<Scenario> scenarios = TestUtils.getScenarios(new HelloWorldScenario(), 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.run();

        assertMinimumThroughput(2000, runner.throughput());
    }

    class HelloWorldScenario extends BaseScenario {
        public void run() {
            System.out.print("Hello World ");
            notifyComplete();
        }
    }
}
