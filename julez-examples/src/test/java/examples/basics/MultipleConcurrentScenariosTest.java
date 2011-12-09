package examples.basics;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import static uk.co.acuminous.julez.scenario.source.ScenarioRepeater.ScenarioRepeaterUnit.REPETITIONS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.MultiConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public class MultipleConcurrentScenariosTest {

    @Test
    public void demonstrateRunningMultipleScenariosConcurrently() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        HelloWorldScenario helloWorldScenario = new HelloWorldScenario();
        helloWorldScenario.register(new FanOutPipe(monitor1, combinedMonitor));
        
        ScenarioSource helloWorldScenarios = new ScenarioRepeater(helloWorldScenario).limitTo(100, REPETITIONS);
        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios).allocate(10, THREADS);
        runner1.register(monitor1);
        
        GoodbyeWorldScenario goodbyeWorldScenario = new GoodbyeWorldScenario();
        goodbyeWorldScenario.register(new FanOutPipe(monitor2, combinedMonitor));
        
        ScenarioSource goodbyeWorldScenarios = new ScenarioRepeater(goodbyeWorldScenario).limitTo(100, REPETITIONS);
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
            handler.onEvent(eventFactory.begin());
            System.out.print("Hello World ");
            handler.onEvent(eventFactory.end());
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        
        public void run() {
            handler.onEvent(eventFactory.begin());
            System.out.print("Goodbye World ");
            handler.onEvent(eventFactory.end());
        }
    }
}
