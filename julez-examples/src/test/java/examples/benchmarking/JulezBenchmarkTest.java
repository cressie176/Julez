package examples.benchmarking;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.net.URL;

import org.junit.Test;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveEmbedderScenario;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import examples.jbehave.CalculatorSteps;

// TODO Cleanup
public class JulezBenchmarkTest extends BenchmarkTestCase {

    private int oneMillionTimes = 1000000;
    private int oneThousandTimes = 1000;
    private ConcurrentScenarioRunner runner;

    @Test
    public void benchmarkSingleThreadedConcurrentScenarioRunnerUsingNoOpScenario() {
                
        benchmark(new NoOpScenario(), oneMillionTimes);
        
        System.out.println(String.format("%d x Single threaded NoOp Scenarios took %dms", oneMillionTimes, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkMultiThreadedConcurrentScenarioRunnerWith10ThreadsUsingNoOpScenario() {
        
        runner = new ConcurrentScenarioRunner().allocate(10, THREADS);        
        
        benchmark(new NoOpScenario(), oneMillionTimes);
        
        System.out.println(String.format("%d x Multi threaded(%d) NoOp Scenarios took %dms", oneMillionTimes, 10, durationMonitor.getDuration()));
    } 
    
    @Test
    public void benchmarkSingleThreadedConcurrentScenarioRunnerSleepingNoOpScenario() {
                
        benchmark(new SleepingScenario(10, MILLISECONDS), oneThousandTimes);
        
        System.out.println(String.format("%d x Single threaded Sleep Scenarios took %dms", oneThousandTimes, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkMultiThreadedConcurrentScenarioRunnerWith10ThreadsSleepingNoOpScenario() {
        
        runner = new ConcurrentScenarioRunner().allocate(10, THREADS);        
        
        benchmark(new SleepingScenario(10, MILLISECONDS), oneThousandTimes);
        
        System.out.println(String.format("%d x Multi threaded(%d) Sleep Scenarios took %dms", oneThousandTimes, 10, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkJBehaveStoryRunnerScenario() {
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveStoryRunnerScenario scenario = new JBehaveStoryRunnerScenario(scenarioLocation, "jbehave/calculator.txt", new CalculatorSteps());
        
        benchmark(scenario, oneThousandTimes);
        
        System.out.println(String.format("%d x Single threaded JBehave Story Runner Scenarios took %dms", oneThousandTimes, durationMonitor.getDuration()));        
    }
        
    @Test
    public void benchmarkJBehaveEmbedderScenario() {
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave/calculator.txt", new CalculatorSteps());
        
        benchmark(scenario, oneThousandTimes);
        
        System.out.println(String.format("%d x Single threaded JBehave Embedder Scenarios took %dms", oneThousandTimes, durationMonitor.getDuration()));        
    }    
    
    @Override
    protected ConcurrentScenarioRunner getScenarioRunner() {
        return runner == null ? super.getScenarioRunner() : runner;
    }
}
