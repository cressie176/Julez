package examples.analysis;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.AsynchronousEventPipe;
import uk.co.acuminous.julez.event.pipe.EventPipe;
import uk.co.acuminous.julez.event.pipe.FanOutEventPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.limiter.InLimboLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousAnalysisTest {

    @Test
    public void demonstrateAsynchronousAnalysis() {
        
        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();
        
        EventPipe asynchronousPipe = new AsynchronousEventPipe().register(throughputMonitor);        
        
        Thread monitorThread = detach(throughputMonitor);        
        
        NoOpScenario scenario = new NoOpScenario();
        
        InLimboLimiter limiter = new InLimboLimiter()
            .block(new ScenarioRepeater(scenario))        
            .when(5000, SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED)            
            .unblockWhen(2500, SCENARIOS_ARE_DEQUEUED_BUT_NOT_STARTED);
        
        scenario.register(new FanOutEventPipe(asynchronousPipe, limiter));
        
        new ConcurrentScenarioRunner().register(asynchronousPipe).allocate(4, THREADS).queue(limiter).runFor(5, SECONDS).start();
        
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
