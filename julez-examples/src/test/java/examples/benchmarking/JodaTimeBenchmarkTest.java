package examples.benchmarking;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;

public class JodaTimeBenchmarkTest extends BenchmarkTestCase {

    private final int repetitions = 1000000;

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
        benchmark(scenario, repetitions);
    }    
}
