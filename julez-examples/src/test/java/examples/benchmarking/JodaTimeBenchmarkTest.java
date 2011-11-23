package examples.benchmarking;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.DurationMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class JodaTimeBenchmarkTest {

    private final int repetitions = 100000;
    private final DurationMonitor durationMonitor = new DurationMonitor();

    @Test
    public void benchmarkConstruction() {
        
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
                new DateTime();
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x new DateTime() took %dms", repetitions, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkRolling() {
        
        Scenario scenario = new BaseScenario() {
            private DateTime dateTime = new DateTime(DateTimeZone.forID("Europe/London"));
            
            @Override public void run() {
                dateTime = dateTime.plusDays(10);
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x dateTime = dateTime.plusDays(10) took %dms", repetitions, durationMonitor.getDuration()));
    }    
    

    private void benchmark(Scenario scenario) {
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, repetitions);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(durationMonitor);
        runner.queue(scenarios).go();
    }    
}
