package examples.analysis;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.acuminous.julez.util.JulezSugar.IN_LIMBO_SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.AsynchronousPipe;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.limiter.InLimboLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousAnalysisTest {

    @Test
    public void demonstrateAsynchronousAnalysis() {
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        
        AsynchronousPipe asynchronousPipe = new AsynchronousPipe();
        asynchronousPipe.register(throughputMonitor);        
        
        Thread monitorThread = detach(throughputMonitor);        
        
        NoOpScenario scenario = new NoOpScenario();
        ScenarioRepeater scenarios = new ScenarioRepeater(scenario);
        InLimboLimiter limiter = new InLimboLimiter().applyLimitOf(5000, IN_LIMBO_SCENARIOS).to(scenarios).liftLimitAt(2500, IN_LIMBO_SCENARIOS);
        
        FanOutPipe fanOutPipe = new FanOutPipe(asynchronousPipe, limiter);        
        scenario.register(fanOutPipe);
        
        new ConcurrentScenarioRunner()
            .register(asynchronousPipe)
            .allocate(3, THREADS)
            .queue(limiter)
            .runFor(10, SECONDS)
            .go();
        
        monitorThread.interrupt();
        
    }

    private Thread detach(final ThroughputMonitor throughputMonitor) {
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
