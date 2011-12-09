package uk.co.acuminous.julez.event.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.marshalling.json.JsonEventTranslator;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.JmsTestUtils;
import uk.co.acuminous.julez.test.TestEventRepository;

public class JmsEventSourceTest {

    private QueueConnectionFactory connectionFactory;
    private JmsEventSource listener;
    private TestEventRepository repository;
    private JmsEventHandler jmsSender;
    
    @Before
    public void init() {
        JmsTestUtils.createBroker(); 
        
        JsonEventTranslator marshaller = new JsonEventTranslator();        
        
        connectionFactory = JmsTestUtils.getConnectionFactory();
        repository = new TestEventRepository();                

        listener = new JmsEventSource(connectionFactory, marshaller);
        listener.setShutdownDelay(1, SECONDS);
        listener.register(repository);
        listener.listen();        
        
        jmsSender = new JmsEventHandler(connectionFactory, marshaller);        
    }
    
    @After
    public void nuke() throws Exception {
        JmsTestUtils.nukeBroker(); 
    }
    
    @Test
    public void scenarioEventsAreRecreated() throws InterruptedException {
        jmsSender.onEvent(new ScenarioEventFactory().pass());
        
        listener.shutdownWhenEmpty();
        
        assertEquals(1, repository.count());
        assertEquals(ScenarioEvent.PASS, repository.first().getType());
    }
    
    @Test
    public void scenarioRunnerEventsAreRecreated() throws InterruptedException {
        jmsSender.onEvent(new ScenarioRunnerEventFactory().begin());
        
        listener.shutdownWhenEmpty();
        
        assertEquals(1, repository.count());
        assertEquals(ScenarioRunnerEvent.BEGIN, repository.first().getType());        
    }    
}
