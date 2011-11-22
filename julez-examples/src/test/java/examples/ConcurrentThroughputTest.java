package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.CappedScenarioRepeater;

public class ConcurrentThroughputTest {

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        HelloWorldScenario scenario = new HelloWorldScenario();

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);                        

        ScenarioSource scenarios = new CappedScenarioRepeater(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.registerEventHandler(throughputMonitor);
        runner.queue(scenarios).run();

        assertMinimumThroughput(500, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.pass());
        }
    }
}
