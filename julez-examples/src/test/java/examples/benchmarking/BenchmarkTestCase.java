package examples.benchmarking;

import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;
import uk.co.acuminous.julez.event.handler.ScenarioRunnerDurationMonitor;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.executor.SynchronousScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public abstract class BenchmarkTestCase {

    protected ScenarioRunnerDurationMonitor durationMonitor = new ScenarioRunnerDurationMonitor();        
    
    public void warmUp(ScenarioExecutor executor) {     
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
            }            
        };        
        ScenarioSource sceanrios = new ScenarioRepeater().repeat(scenario).upTo(1000, TIMES);
        benchmark(sceanrios, executor);          
    }    

    protected void benchmark(Scenario scenario, int numberOfRepetitions) {
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(numberOfRepetitions, TIMES);
                                                      
        ScenarioExecutor executor = new SynchronousScenarioExecutor();
        warmUp(executor);

        benchmark(scenarios, executor);
    }
    
    protected void benchmark(Scenario scenario, int numberOfRepetitions, int numberOfThreads) {
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(numberOfRepetitions, TIMES);
                                                      
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(10, THREADS).initialise();
        warmUp(executor);
        executor.initialise();
        
        benchmark(scenarios, executor);
    }
    
    private void benchmark(ScenarioSource scenarios, ScenarioExecutor executor) {
        SimpleScenarioRunner runner = new SimpleScenarioRunner().assign(executor);        
        runner.register(durationMonitor);
        runner.queue(scenarios).start();
    }    

    protected SimpleScenarioRunner getScenarioRunner() {
        return new SimpleScenarioRunner().assign(new SynchronousScenarioExecutor());
    }    
}
