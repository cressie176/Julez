package examples;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.util.UUID;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventRecorder;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class CorrelatedScenarioTest {
    
    @Test
    public void demonstrateHowToCorrelateEventsTest() {

        String correlationId = UUID.randomUUID().toString();
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(correlationId);
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);

        EventRecorder recorder = new EventRecorder();        
        
        HelloWorldScenario scenario = new HelloWorldScenario();
        scenario.useEventFactory(scenarioEventFactory);
        scenario.register(recorder);                        

        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.useEventFactory(scenarioRunnerEventFactory);
        runner.register(recorder);
        runner.queue(scenarios).allocate(10, THREADS).go();

        for (Event event : recorder.getEvents()) {
            assertEquals(correlationId, event.getCorrelationId());
        }
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.end());
        }
    }
}
