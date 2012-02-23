package uk.co.acuminous.julez.scenario.limiter;

import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.NoOpScenario;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public class ThroughputLimiterTest {

    @Test
    public void aproximatelyLimitsThroughputToSpecifiedFrequency() {      
        
        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();

        
        Scenario scenario = new NoOpScenario().register(throughputMonitor);
        
        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(100, TIMES);                                                                     
        
        ThroughputLimiter limiter = new ThroughputLimiter().applyLimitOf(100, SCENARIOS).perSecond().to(scenarios);
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(throughputMonitor).queue(limiter).start();
        
        assertTrue("Throughput was not limited: " + throughputMonitor.getThroughput(), throughputMonitor.getThroughput() <= 100);
        assertTrue("Throughput was not limited: " + throughputMonitor.getThroughput(), throughputMonitor.getThroughput() >= 99);
    }  
}
