package examples;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import java.net.URL;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
import uk.co.acuminous.julez.test.WebTestCase;
import examples.jbehave.Scenario1Steps;

public class ConcurrentJBehaveTest extends WebTestCase {

    @Test
    public void demonstrateAConcurrentJBehaveTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.register(throughputMonitor);        
        
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
        runner.queue(scenarios).allocate(10, THREADS).runFor(30, SECONDS).go();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
    }
}
