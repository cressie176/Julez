package examples.analysis;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.AsynchronousEventPipe;
import uk.co.acuminous.julez.event.pipe.EventPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousAnalysisTest {

    @Test
    public void demonstrateAsynchronousAnalysis() {
        
        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();
        
        EventPipe asynchronousPipe = new AsynchronousEventPipe().register(throughputMonitor);        
        
        Thread monitorThread = detach(throughputMonitor);        
        
        Scenario scenario = new NoOpScenario().register(asynchronousPipe);
        
        ScenarioRepeater scenarios = new ScenarioRepeater(scenario);
        
        new ConcurrentScenarioRunner().register(asynchronousPipe).allocate(4, THREADS).limitWorkQueueTo(100, SCENARIOS).queue(scenarios).runFor(5, SECONDS).start();
        
        monitorThread.interrupt();        
    }

    private Thread detach(final ScenarioThroughputMonitor throughputMonitor) {
        return ConcurrencyUtils.start(new Runnable() {
            @Override
            public void run() {
                boolean ok = true;
                while(ok) {
                    System.out.println("Throughput: " + throughputMonitor.getThroughput());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        ok = false;
                    }
                }
            }            
        });
    }
    
}
