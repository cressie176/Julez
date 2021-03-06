package examples.basics;

import static uk.co.acuminous.julez.util.JulezSugar.*;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public class ConcurrentThroughputTest {

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        
        Scenario scenario = new HelloWorldScenario().register(throughputMonitor);                        

        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(100, SCENARIOS);        
        
        new ConcurrentScenarioRunner().register(throughputMonitor).queue(scenarios).allocate(10, THREADS).go();

        assertMinimumThroughput(500, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        public void run() {
            handler.onEvent(eventFactory.begin());
            System.out.print("Hello World ");
            handler.onEvent(eventFactory.end());
        }
    }
}
