package examples;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.URL;
import java.util.Deque;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario1Steps;

public class JBehavePerformanceTest extends WebTestCase {

    @Test
    public void demonstrateASimpleJBehavePerformanceTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        Deque<Scenario> scenarios = TestUtils.getScenarios(scenario, 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios).timeOutAfter(30, SECONDS);
        runner.run();

        assertMinimumThroughput(5, runner.throughput());
    }
}
