package examples.basics;

import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;
import static uk.co.acuminous.julez.util.JulezSugar.TIMES;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;

public class ConcurrentThroughputTest {

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        ScenarioThroughputMonitor throughputMonitor = new ScenarioThroughputMonitor();
        
        Scenario scenario = new HelloWorldScenario().register(throughputMonitor);                        

        ScenarioSource scenarios = new ScenarioRepeater().repeat(scenario).upTo(100, TIMES);        
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(10, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(throughputMonitor).queue(scenarios).start();
        
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
