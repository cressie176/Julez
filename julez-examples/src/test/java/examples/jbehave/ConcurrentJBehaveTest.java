package examples.jbehave;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.LightJBehaveScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.PrePopulatedScenarioSource;

public class ConcurrentJBehaveTest {

    @Test
    public void demonstrateAConcurrentJBehaveTest() {

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        ResultMonitor resultMonitor = new ResultMonitor();
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 1000; i++) {
            LightJBehaveScenario scenario = new LightJBehaveScenario(scenarioLocation, "jbehave/calculator.txt", new CalculatorSteps());
            scenario.registerAll(throughputMonitor, resultMonitor);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new PrePopulatedScenarioSource(list);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
        runner.queue(scenarios).allocate(3, THREADS).go();

        assertMinimumThroughput(100, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());
        
    }
}
