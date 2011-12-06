package examples.analysis;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.AsynchronousPipe;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.source.InfiniteScenarioRepeater;
import uk.co.acuminous.julez.scenario.source.InflightLimiter;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class AsynchronousAnalysisTest {

    @Test
    public void demonstrateAsynchronousAnalysis() {
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        AsynchronousPipe asynchronousPipe = new AsynchronousPipe();
        asynchronousPipe.register(throughputMonitor);        
        
        Thread monitorThread = detach(throughputMonitor);        
        
        Scenario scenario = new NoOpScenario();
        InflightLimiter scenarios = new InflightLimiter(new InfiniteScenarioRepeater(scenario), 5000, 2500);        
        FanOutPipe fanOutPipe = new FanOutPipe(asynchronousPipe, scenarios);        
        scenario.register(fanOutPipe);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(asynchronousPipe);
        runner.allocate(3, THREADS).queue(scenarios).runFor(10, SECONDS).go();
        
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
