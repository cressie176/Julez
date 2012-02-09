package examples.basics;

import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerScenario;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;


public class MultipleConcurrentScenariosTest {

    @Test
    public void demonstrateRunningMultipleScenariosConcurrently() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        Scenario helloWorldScenario = new HelloWorldScenario().register(new FanOutPipe(monitor1, combinedMonitor));
        
        ScenarioSource helloWorldScenarios = new SizeLimiter().limit(new ScenarioRepeater(helloWorldScenario)).to(100, SCENARIOS);
        ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner().register(monitor1).allocate(10, THREADS).queue(helloWorldScenarios);
        
        Scenario goodbyeWorldScenario = new GoodbyeWorldScenario().register(new FanOutPipe(monitor2, combinedMonitor));
        
        ScenarioSource goodbyeWorldScenarios = new SizeLimiter().limit(new ScenarioRepeater(goodbyeWorldScenario)).to(100, SCENARIOS);
        ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner().register(monitor2).allocate(10, THREADS).queue(goodbyeWorldScenarios);

        ScenarioSource concurrentScenarios = new ScenarioHopper(new ScenarioRunnerScenario(runner1), new ScenarioRunnerScenario(runner2));
        new ConcurrentScenarioRunner().queue(concurrentScenarios).register(combinedMonitor).start();

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
