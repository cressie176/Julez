package examples;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class MultipleConcurrentScenariosTest {

    @Test
    public void demonstrateRunningMultipleScenariosConcurrently() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        HelloWorldScenario helloWorldScenario = new HelloWorldScenario();
        helloWorldScenario.register(monitor1, combinedMonitor);
        
        ScenarioSource helloWorldScenarios = new SizedScenarioRepeater(helloWorldScenario, 100);
        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios).allocate(10, THREADS);
        runner1.register(monitor1);
        
        GoodbyeWorldScenario goodbyeWorldScenario = new GoodbyeWorldScenario();
        goodbyeWorldScenario.register(monitor2, combinedMonitor);
        
        ScenarioSource goodbyeWorldScenarios = new SizedScenarioRepeater(goodbyeWorldScenario, 100);
        ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner().queue(goodbyeWorldScenarios).allocate(10, THREADS);
        runner2.register(monitor2);

        MultiConcurrentScenarioRunner runner = new MultiConcurrentScenarioRunner(runner1, runner2);
        runner.register(combinedMonitor);
        runner.go();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.end());
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Goodbye World ");
            raise(eventFactory.end());
        }
    }
}
