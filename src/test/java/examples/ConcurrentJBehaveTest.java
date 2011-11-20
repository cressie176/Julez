package examples;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.URL;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario1Steps;

public class ConcurrentJBehaveTest extends WebTestCase {

    @Test
    public void demonstrateAConcurrentJBehaveTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios).timeOutAfter(30, SECONDS);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
    }
}
