package examples.benchmarking;

import static uk.co.acuminous.julez.util.JulezSugar.TIMES;

import org.junit.Before;

import uk.co.acuminous.julez.event.handler.ScenarioRunnerDurationMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public abstract class BenchmarkTestCase {

    protected ScenarioRunnerDurationMonitor durationMonitor = new ScenarioRunnerDurationMonitor();        
    
    @Before
    public void warmUpJulez() {     
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
            }            
        };        
        benchmark(scenario, 1000);          
    }    
    
    protected void benchmark(Scenario scenario, int n) {
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).atMost(n, TIMES);
        
        ConcurrentScenarioRunner runner = getScenarioRunner();
        runner.register(durationMonitor);
        runner.queue(scenarios).start();
    }

    protected ConcurrentScenarioRunner getScenarioRunner() {
        return new ConcurrentScenarioRunner();
    }    
}
