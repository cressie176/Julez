package examples.benchmarking;

import org.junit.Before;

import uk.co.acuminous.julez.event.handler.DurationMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import static uk.co.acuminous.julez.util.JulezSugar.*;

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
    
    protected void benchmark(Scenario scenario, int n) {
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(n, SCENARIOS);
        
        ConcurrentScenarioRunner runner = getScenarioRunner();
        runner.register(durationMonitor);
        runner.queue(scenarios).go();
    }

    protected ConcurrentScenarioRunner getScenarioRunner() {
        return new ConcurrentScenarioRunner();
    }    
}
