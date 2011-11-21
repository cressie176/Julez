package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.ThroughputLimiter;
import uk.co.acuminous.julez.test.InvocationCountingScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import uk.co.acuminous.julez.test.TestUtils;

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
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);
        
        int fiftyPerSecond = 1000 / 50;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, fiftyPerSecond, MILLISECONDS);        
        runner.queue(limiter).run();
        
        assertEquals(50, throughputMonitor.getThroughput());
    }
    
    @Test
    public void cappingThroughputForLongRunningScenariosDoesntCauseLag() {
        SleepingScenario scenario = new SleepingScenario();
        scenario.registerEventHandler(throughputMonitor);
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 5);
        
        int twoPerSecond = 1000 / 2;
        ThroughputLimiter limiter = new ThroughputLimiter(scenarios, twoPerSecond, MILLISECONDS);        
        runner.queue(limiter).run();
        
        assertEquals(2, throughputMonitor.getThroughput());
    }    
}
