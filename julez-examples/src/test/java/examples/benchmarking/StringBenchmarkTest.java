package examples.benchmarking;

import org.junit.Test;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;

@SuppressWarnings("unused")
public class StringBenchmarkTest extends BenchmarkTestCase {

    private int repetitions = 1000000;      
    
    @Test
    public void benchmarkConcatenationUsingPlus() {
        
        benchmark(new BaseScenario() {            
            @Override public void run() {
                String s = "1foo" + "1bar";
            }            
        });
        
        System.out.println(String.format("%d x \"1foo\" + \"1bar\" took %dms", repetitions, durationMonitor.getDuration()));
    }
    
    @Test
    public void benchmarkConcatenationUsingStringConcat() {
        
        benchmark(new BaseScenario() {
            @Override public void run() {
                String s = "2foo".concat("bar");
            }            
        });
        
        System.out.println(String.format("%d x \"2foo\".concat(\"2bar\") took %dms", repetitions, durationMonitor.getDuration()));
    }     
    
    @Test
    public void benchmarkConcatenationUsingStringBuffer() {
        
        benchmark(new BaseScenario() {
            @Override public void run() {
                String s = new StringBuffer().append("3foo").append("3bar").toString();
            }            
        });
        
        System.out.println(String.format("%d x new StringBuffer().append(\"3foo\").append(\"3bar\").toString() took %dms", repetitions, durationMonitor.getDuration()));
    }    
        
    @Test
    public void benchmarkConcatenationUsingStringBuilder() {
        
        benchmark(new BaseScenario() {
            @Override public void run() {
                String s = new StringBuilder().append("4foo").append("4bar").toString();
            }            
        });
        
        System.out.println(String.format("%d x new StringBuilder().append(\"4foo\").append(\"4bar\").toString() took %dms", repetitions, durationMonitor.getDuration()));
    }  
        
    @Test
    public void benchmarkConcatenationUsingStringFormat() {        
        
        benchmark(new BaseScenario() {
            @Override public void run() {
                String s = String.format("%s%s", "5foo", "5bar");
            }            
        });
        
        System.out.println(String.format("%d x String.format(\"%%s%%s\", \"5foo\", \"5bar\") took %dms", repetitions, durationMonitor.getDuration()));
    } 
    
    private void benchmark(Scenario scenario) {
        benchmark(scenario, repetitions);
    }
}
