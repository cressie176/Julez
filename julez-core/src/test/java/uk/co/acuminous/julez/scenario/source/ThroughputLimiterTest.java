package uk.co.acuminous.julez.scenario.source;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.InvocationCountingScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.SleepingScenario;
import uk.co.acuminous.julez.scenario.source.ThroughputLimiter;

public class ThroughputLimiterTest {

    private ThroughputMonitor throughputMonitor;
    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {
        throughputMonitor = new ThroughputMonitor();
        
        runner = new ConcurrentScenarioRunner();
        runner.registerEventHandler(throughputMonitor);        
    }
    
    @Test
    public void capsThroughputToSpecifiedFrequency() {        
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        scenario.registerEventHandler(throughputMonitor);
        
        ScenarioSource scenarios = new CappedScenarioRepeater(scenario, 100);
        
        int fiftyPerSecond = 1000 / 50;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, fiftyPerSecond, MILLISECONDS);        
        runner.queue(limiter).run();
        
        assertEquals(50, throughputMonitor.getThroughput());
    }
    
    @Test
    public void cappingThroughputForLongRunningScenariosDoesntCauseLag() {
        SleepingScenario scenario = new SleepingScenario();
        scenario.registerEventHandler(throughputMonitor);
        
        ScenarioSource scenarios = new CappedScenarioRepeater(scenario, 5);
        
        int twoPerSecond = 1000 / 2;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, twoPerSecond, MILLISECONDS);        
        runner.queue(limiter).run();
        
        assertEquals(2, throughputMonitor.getThroughput());
    }    
}
