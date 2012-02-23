package examples.analysis;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.AsynchronousEventPipe;
import uk.co.acuminous.julez.event.pipe.EventPipe;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.limiter.DurationLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousAnalysisTest {

    @Test
    public void demonstrateAsynchronousAnalysis() {
        
        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();
        
        EventPipe asynchronousPipe = new AsynchronousEventPipe().register(throughputMonitor);        
        
        Thread monitorThread = detach(throughputMonitor);        
        
        Scenario scenario = new NoOpScenario().register(asynchronousPipe);
        
        ScenarioSource scenarios = new DurationLimiter().limit(new ScenarioRepeater(scenario)).to(5, SECONDS);
                
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS).limitWorkQueueTo(100, SCENARIOS);
        
        new SimpleScenarioRunner().assign(executor).register(asynchronousPipe).queue(scenarios).start();
                
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
