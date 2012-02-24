package examples.basics;

import static java.lang.Math.max;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutEventPipe;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.instruction.ScenarioRunnerStarter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;


public class MultipleConcurrentScenariosTest {

    @Test
    public void demonstrateRunningScenariosConcurrentlyFromMultipleRunners() {

        ScenarioThroughputMonitor monitor1 = new ScenarioThroughputMonitor();
        ScenarioThroughputMonitor monitor2 = new ScenarioThroughputMonitor();
        ScenarioThroughputMonitor overallMonitor = new ScenarioThroughputMonitor();        
                
        Scenario helloWorldScenario = new HelloWorldScenario().register(new FanOutEventPipe(monitor1, overallMonitor));        
        ScenarioSource helloWorldScenarios = new ScenarioRepeater().repeat(helloWorldScenario).upTo(1000, TIMES);
        
        ScenarioExecutor executor1 = new ConcurrentScenarioExecutor().allocate(10, THREADS);        
        ScenarioRunner runner1 = new SimpleScenarioRunner().assign(executor1).register(monitor1).queue(helloWorldScenarios);
         
        Scenario goodbyeWorldScenario = new GoodbyeWorldScenario().register(new FanOutEventPipe(monitor2, overallMonitor));        
        ScenarioSource goodbyeWorldScenarios = new ScenarioRepeater().repeat(goodbyeWorldScenario).upTo(1000, TIMES);
        
        ScenarioExecutor executor2 = new ConcurrentScenarioExecutor().allocate(10, THREADS);        
        ScenarioRunner runner2 = new SimpleScenarioRunner().assign(executor2).register(monitor2).queue(goodbyeWorldScenarios);
        
        ScenarioSource concurrentScenarios = new ScenarioHopper(new ScenarioRunnerStarter(runner1), new ScenarioRunnerStarter(runner2));

        ScenarioExecutor executor3 = new ConcurrentScenarioExecutor().allocate(2, THREADS);                
        new SimpleScenarioRunner().assign(executor3).queue(concurrentScenarios).register(overallMonitor).start();        
                
        assertMinimumThroughput(1000, monitor1.getThroughput());
        assertMinimumThroughput(1000, monitor2.getThroughput());
        
        assertTrue("Overall throughput was not greater than individual throughput", overallMonitor.getThroughput() > max(monitor1.getThroughput(), monitor2.getThroughput()));
        assertTrue("Overall throughput was impossibily high", overallMonitor.getThroughput() <= monitor1.getThroughput() + monitor2.getThroughput());
        
    }

    class HelloWorldScenario extends BaseScenario {
        
        @SuppressWarnings("unused")
        private int counter = 0;
        
        public void run() {
            handler.onEvent(eventFactory.begin());
            // System.out.println(String.format("%3s Hello World", ++counter));
            handler.onEvent(eventFactory.end());
        }
    }

    class GoodbyeWorldScenario extends BaseScenario {
        
        @SuppressWarnings("unused")
        private int counter = 0;
        
        public void run() {
            handler.onEvent(eventFactory.begin());
            // System.out.println(String.format("%3s Goodbye World", ++counter));
            handler.onEvent(eventFactory.end());
        }
    }
}
