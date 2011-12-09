package uk.co.acuminous.julez.scenario.source;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.util.JulezSugar.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.limiter.ThroughputLimiter;
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
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(100, SCENARIOS).to(new ScenarioRepeater(scenario));                                                                     
        
        ThroughputLimiter limiter = new ThroughputLimiter().applyLimitOf(50, SCENARIOS).perSecond().to(scenarios);
        
        runner.queue(limiter).allocate(10, THREADS).go();
        
        assertEquals(50, throughputMonitor.getThroughput());
    }
    
    @Test
    public void limittingThroughputForLongRunningScenariosDoesntCauseLag() {
        SleepingScenario scenario = new SleepingScenario();
        scenario.register(throughputMonitor);
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(5, SCENARIOS).to(new ScenarioRepeater(scenario));                                                                     
        
        ThroughputLimiter limiter = new ThroughputLimiter().applyLimitOf(2, SCENARIOS).perSecond().to(scenarios);        
        
        runner.queue(limiter).allocate(10, THREADS).go();
        
        assertEquals(2, throughputMonitor.getThroughput());
    }    
}
