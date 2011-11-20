package examples;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.URL;
import java.util.UUID;

import org.junit.Test;

import uk.co.acuminous.julez.event.handlers.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario1Steps;

public class JBehavePerformanceTest extends WebTestCase {

    @Test
    public void demonstrateASimpleJBehavePerformanceTest() {

        String correlationId = UUID.randomUUID().toString();
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationId);
                
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioEventFactory, scenarioLocation, "scenario1.txt", new Scenario1Steps());
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).queue(scenarios).timeOutAfter(30, SECONDS);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
    }
}
