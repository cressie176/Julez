package examples.jbehave;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioResultMonitor;
import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutEventPipe;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
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
        
        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();  
        ScenarioResultMonitor resultMonitor = new ScenarioResultMonitor();
        FanOutEventPipe monitors = new FanOutEventPipe(throughputMonitor, resultMonitor);
        
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 500; i++) {
            JBehaveStoryRunnerScenario scenario = new JBehaveStoryRunnerScenario(scenarioLocation, "examples/jbehave/calculator.story", new CalculatorSteps());
            scenario.register(monitors);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new ScenarioHopper(list);  
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(throughputMonitor).queue(scenarios).start();
        
        assertMinimumThroughput(100, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }
    
    @Test
    public void demonstrateTheJBehaveEmbedderScenario() {

        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();  
        ScenarioResultMonitor resultMonitor = new ScenarioResultMonitor();
        FanOutEventPipe monitors = new FanOutEventPipe(throughputMonitor, resultMonitor);
                
        List<Scenario> list = new ArrayList<Scenario>();
        for (int i = 0; i < 500; i++) {
            JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "examples/jbehave/calculator.story", new CalculatorSteps());
            scenario.register(monitors);
            list.add(scenario);
        }
        
        ScenarioSource scenarios = new ScenarioHopper(list);
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(throughputMonitor).queue(scenarios).start();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
        assertPassMark(100, resultMonitor.getPercentage());        
    }    
}
