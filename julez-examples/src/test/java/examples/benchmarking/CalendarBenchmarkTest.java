package examples.benchmarking;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;

public class CalendarBenchmarkTest extends BenchmarkTestCase {

    private int repetitions = 1000000;

    @Test
    public void benchmarkConstruction() {
        
        benchmark(new BaseScenario() {
            @Override public void run() {
                Calendar.getInstance();
            }            
        });
        
        System.out.println(String.format("%d x Calendar.getInstance() took %dms", repetitions, durationMonitor.getDuration()));
    }

    @Test
    public void benchmarkRolling() {
        
        benchmark(new BaseScenario() {
            private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));            
            @Override public void run() {
                calendar.roll(Calendar.DAY_OF_MONTH, 10);
            }            
        });
        
        System.out.println(String.format("%d x calendar.roll(DAY_OF_MONTH, 10) took %dms", repetitions, durationMonitor.getDuration()));
    }

    private void benchmark(Scenario scenario) {
        benchmark(scenario, repetitions);
    }    
    
}
