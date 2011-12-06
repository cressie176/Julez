package examples.basics;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class ConcurrentThroughputTest {

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        HelloWorldScenario scenario = new HelloWorldScenario();

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.register(throughputMonitor);                        

        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
        runner.queue(scenarios).allocate(10, THREADS).go();

        assertMinimumThroughput(500, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public void run() {
            onEvent(eventFactory.begin());
            System.out.print("Hello World ");
            onEvent(eventFactory.end());
        }
    }
}
