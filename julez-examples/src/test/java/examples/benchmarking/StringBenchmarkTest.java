package examples.benchmarking;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.DurationMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

@SuppressWarnings("unused")
public class StringBenchmarkTest {

    private int repetitions = 100000;
    private DurationMonitor durationMonitor = new DurationMonitor();;


    @Test
    public void benchmarkConcatenationUsingPlus() {
        
        Scenario scenario = new BaseScenario() {            
            @Override public void run() {
                String s = "1foo" + "1bar";
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x \"1foo\" + \"1bar\" took %dms", repetitions, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkConcatenationUsingStringConcat() {
        
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
                String s = "2foo".concat("bar");
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x \"2foo\".concat(\"2bar\") took %dms", repetitions, durationMonitor.getDuration()));
    }     
    
    @Test
    public void benchmarkConcatenationUsingStringBuffer() {
        
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
                String s = new StringBuffer().append("3foo").append("3bar").toString();
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x new StringBuffer().append(\"3foo\").append(\"3bar\").toString() took %dms", repetitions, durationMonitor.getDuration()));
    }    
        
    @Test
    public void benchmarkConcatenationUsingStringBuilder() {
        
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
                String s = new StringBuilder().append("4foo").append("4bar").toString();
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x new StringBuilder().append(\"4foo\").append(\"4bar\").toString() took %dms", repetitions, durationMonitor.getDuration()));
    }  
        
    @Test
    public void benchmarkConcatenationUsingStringFormat() {        
        
        Scenario scenario = new BaseScenario() {
            @Override public void run() {
                String s = String.format("%s%s", "5foo", "5bar");
            }            
        };
        
        benchmark(scenario);
        
        System.out.println(String.format("%d x String.format(\"%%s%%s\", \"5foo\", \"5bar\") took %dms", repetitions, durationMonitor.getDuration()));
    }

    private void benchmark(Scenario scenario) {
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, repetitions);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(durationMonitor);
        runner.queue(scenarios).go();
    }     
    
}
