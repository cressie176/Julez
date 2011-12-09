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
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit;
import uk.co.acuminous.julez.scenario.JBehaveEmbedderScenario;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;


public class ConcurrentJBehaveTest {

    private URL scenarioLocation;

    @Before
    public void init() {
        scenarioLocation = codeLocationFromClass(this.getClass());
    }
    
    @Test
    public void demonstrateTheJBehaveStoryRunnerScenario() {
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();  
        ResultMonitor resultMonitor = new ResultMonitor();
        FanOutPipe monitors = new FanOutPipe(throughputMonitor, resultMonitor);
        
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 1000; i++) {
            JBehaveStoryRunnerScenario scenario = new JBehaveStoryRunnerScenario(scenarioLocation, "examples/jbehave/calculator.story", new CalculatorSteps());
            scenario.register(monitors);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new ScenarioHopper(list);  
        
        new ConcurrentScenarioRunner()
            .register(throughputMonitor)
            .allocate(3, ConcurrencyUnit.THREADS)
            .queue(scenarios)
            .go();

        assertMinimumThroughput(100, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }
    
    @Test
    public void demonstrateTheJBehaveEmbedderScenario() {

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();  
        ResultMonitor resultMonitor = new ResultMonitor();
        FanOutPipe monitors = new FanOutPipe(throughputMonitor, resultMonitor);
                
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 1000; i++) {
            JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "examples/jbehave/calculator.story", new CalculatorSteps());
            scenario.register(monitors);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new ScenarioHopper(list);
        
        new ConcurrentScenarioRunner()
            .register(throughputMonitor)
            .allocate(3, ConcurrencyUnit.THREADS)
            .queue(scenarios)
            .go();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }    
}
