package uk.co.acuminous.julez.scenario.limiter;

import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public class ThroughputLimiterTest {

    private ScenarioThroughputMonitor throughputMonitor;
    private ConcurrentScenarioRunner runner;

    @Before
    public void init() {
        throughputMonitor = new ScenarioThroughputMonitor();
        
        runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);        
    }
    
    @Test
    public void limitsThroughputToSpecifiedFrequency() {        
        Scenario scenario = new NoOpScenario().register(throughputMonitor);
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(100, SCENARIOS);                                                                     
        
        ThroughputLimiter limiter = new ThroughputLimiter().applyLimitOf(100, SCENARIOS).perSecond().to(scenarios);
        
        runner.queue(limiter).allocate(4, THREADS).start();
        
        assertTrue("Throughput was not limited", throughputMonitor.getThroughput() <= 100);
        assertTrue("Throughput was not limited", throughputMonitor.getThroughput() >= 99);
    }  
}
