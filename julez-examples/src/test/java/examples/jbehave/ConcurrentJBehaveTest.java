package examples.jbehave;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveEmbedderScenario;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.PrePopulatedScenarioSource;

public class ConcurrentJBehaveTest {

    private ThroughputMonitor throughputMonitor;
    private ResultMonitor resultMonitor;
    private URL scenarioLocation;
    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {
        throughputMonitor = new ThroughputMonitor();
        resultMonitor = new ResultMonitor();
        scenarioLocation = codeLocationFromClass(this.getClass());
        
        runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
    }
    
    @Test
    public void demonstrateTheJBehaveStoryRunnerScenario() {
        
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 1000; i++) {
            JBehaveStoryRunnerScenario scenario = new JBehaveStoryRunnerScenario(scenarioLocation, "jbehave/calculator.txt", new CalculatorSteps());
            scenario.registerAll(throughputMonitor, resultMonitor);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new PrePopulatedScenarioSource(list);        
        runner.queue(scenarios).go();

        assertMinimumThroughput(100, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }
    
    @Test
    public void demonstrateTheJBehaveEmbedderScenario() {

        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 1000; i++) {
            JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave/calculator.txt", new CalculatorSteps());
            scenario.registerAll(throughputMonitor, resultMonitor);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new PrePopulatedScenarioSource(list);
        runner.queue(scenarios).go();

        assertMinimumThroughput(10, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }    
}
