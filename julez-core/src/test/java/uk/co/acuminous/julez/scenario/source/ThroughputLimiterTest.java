package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.SleepingScenario;

public class ThroughputLimiterTest {

    private ThroughputMonitor throughputMonitor;
    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {
        throughputMonitor = new ThroughputMonitor();
        
        runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);        
    }
    
    @Test
    public void limitsThroughputToSpecifiedFrequency() {        
        Scenario scenario = new NoOpScenario().register(throughputMonitor);
        
        ScenarioSource scenarios = new ScenarioRepeater(scenario).limitRepetitionsTo(100);                                                                     
        
        int fiftyPerSecond = 1000 / 50;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, fiftyPerSecond, MILLISECONDS);        
        runner.queue(limiter).allocate(10, THREADS).go();
        
        assertEquals(50, throughputMonitor.getThroughput());
    }
    
    @Test
    public void limittingThroughputForLongRunningScenariosDoesntCauseLag() {
        SleepingScenario scenario = new SleepingScenario();
        scenario.register(throughputMonitor);
        
        ScenarioSource scenarios = new ScenarioRepeater(scenario).limitRepetitionsTo(5);                                                                     
        
        int twoPerSecond = 1000 / 2;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, twoPerSecond, MILLISECONDS);        
        runner.queue(limiter).allocate(10, THREADS).go();
        
        assertEquals(2, throughputMonitor.getThroughput());
    }    
}
