package examples.benchmarking;


import org.junit.Before;

import uk.co.acuminous.julez.event.handler.DurationMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public abstract class BenchmarkTestCase {

    protected DurationMonitor durationMonitor = new DurationMonitor();        
    
    @Before
    public void warmUpJulez() {     
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
            }            
        };        
        benchmark(scenario, 1000);          
    }    
    

    protected void benchmark(Scenario scenario, int repetitions) {
        
        ScenarioSource scenarios = new SizeLimiter().applySizeLimit(repetitions).to(new ScenarioRepeater(scenario));
        
        ConcurrentScenarioRunner runner = getScenarioRunner();
        runner.register(durationMonitor);
        runner.queue(scenarios).go();
    }


    protected ConcurrentScenarioRunner getScenarioRunner() {
        return new ConcurrentScenarioRunner();
    }    
}
