package examples.benchmarking;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.net.URL;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.JBehaveEmbedderScenario;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.instruction.SleepScenario;
import examples.jbehave.CalculatorSteps;

public class JulezBenchmarkTest extends BenchmarkTestCase {

    private int fourThreads = 4;
    private int oneHundredThousandTimes = 100000;
    private int oneThousandTimes = 1000;

    @Test
    public void benchmarkSingleThreadedScenarioRunnerUsingNoOpScenario() {
                
        benchmark(new NoOpScenario(), oneHundredThousandTimes);
        
        System.out.println(String.format("%d x Single threaded NoOp Scenarios took %dms", oneHundredThousandTimes, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkMultiThreadedScenarioRunnerWith10ThreadsUsingNoOpScenario() {
                
        benchmark(new NoOpScenario(), oneHundredThousandTimes, fourThreads);
        
        System.out.println(String.format("%d x Multi threaded(%d) NoOp Scenarios took %dms", oneHundredThousandTimes, fourThreads, durationMonitor.getDuration()));
    } 
    
    @Test
    public void benchmarkSingleThreadedScenarioRunnerSleepingNoOpScenario() {
                
        benchmark(new SleepScenario().sleepFor(10, MILLISECONDS), oneThousandTimes);
        
        System.out.println(String.format("%d x Single threaded Sleep Scenarios took %dms", oneThousandTimes, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkMultiThreadedScenarioRunnerWith10ThreadsSleepingNoOpScenario() {       
        
        benchmark(new SleepScenario().sleepFor(10, MILLISECONDS), oneThousandTimes, fourThreads);
        
        System.out.println(String.format("%d x Multi threaded(%d) Sleep Scenarios took %dms", oneThousandTimes, fourThreads, durationMonitor.getDuration()));
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
}
