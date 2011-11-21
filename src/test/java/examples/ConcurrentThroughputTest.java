package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.source.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class ConcurrentThroughputTest {

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        HelloWorldScenario scenario = new HelloWorldScenario();

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);                        

        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

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
