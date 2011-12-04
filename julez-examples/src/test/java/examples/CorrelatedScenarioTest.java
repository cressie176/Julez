package examples;

import static org.junit.Assert.assertEquals;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class CorrelatedScenarioTest {
    
    @Test
    public void demonstrateHowToCorrelateEventsTest() throws UnknownHostException {

        String correlationId = UUID.randomUUID().toString();
        String hostname = InetAddress.getLocalHost().getHostName();
        Map<String, String> data = new HashMap<String, String>();
        data.put("CORRELATION_ID", correlationId);
        data.put("HOSTNAME", hostname);
        
        ScenarioEventFactory scenarioEventFactory = new ScenarioEventFactory(data);
        ScenarioRunnerEventFactory scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(data);

        EventMonitor eventMonitor = new EventMonitor();        
        
        HelloWorldScenario scenario = new HelloWorldScenario();
        scenario.useEventFactory(scenarioEventFactory);
        scenario.register(eventMonitor);                        

        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.useEventFactory(scenarioRunnerEventFactory);
        runner.register(eventMonitor);
        runner.queue(scenarios).allocate(10, THREADS).go();

        for (Event event : eventMonitor.getEvents()) {
            assertEquals(correlationId, event.get("CORRELATION_ID"));
            assertEquals(hostname, event.get("HOSTNAME"));
        }
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public void run() {
            onEvent(eventFactory.begin());
            System.out.print("Hello World ");
            onEvent(eventFactory.end());
        }
    }
}
