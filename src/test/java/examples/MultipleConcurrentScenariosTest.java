package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class MultipleConcurrentScenariosTest {

    @Test
    public void demonstrateRunningMultipleScenariosConcurrently() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        HelloWorldScenario helloWorldScenario = new HelloWorldScenario();
        helloWorldScenario.registerEventHandler(monitor1, combinedMonitor);
        
        Scenarios helloWorldScenarios = TestUtils.getScenarios(helloWorldScenario, 100);
        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios);
        runner1.registerEventHandler(monitor1);
        
        GoodbyeWorldScenario goodbyeWorldScenario = new GoodbyeWorldScenario();
        goodbyeWorldScenario.registerEventHandler(monitor2, combinedMonitor);
        
        Scenarios goodbyeWorldScenarios = TestUtils.getScenarios(goodbyeWorldScenario, 100);
        ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner().queue(goodbyeWorldScenarios);
        runner2.registerEventHandler(monitor2);

        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(runner1, runner2);
        runner.registerEventHandler(combinedMonitor);
        runner.run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.pass());
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Goodbye World ");
            raise(eventFactory.pass());
        }
    }
}
