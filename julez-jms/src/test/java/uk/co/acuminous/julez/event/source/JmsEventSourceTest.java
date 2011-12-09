package uk.co.acuminous.julez.event.source;

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.jms.QueueConnectionFactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.marshalling.json.JsonEventTranslator;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.JmsTestUtils;
import uk.co.acuminous.julez.test.TestEventRepository;
import uk.co.acuminous.julez.test.TestUtils;

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
        ScenarioEvent pass = new ScenarioEventFactory().pass();
        
        jmsSender.onEvent(pass);
        
        listener.shutdownWhenEmpty();

        Assert.assertTrue(TestUtils.checkEvents(new Event[] { pass }, repository));
    }
    
    @Test
    public void scenarioRunnerEventsAreRecreated() throws InterruptedException {
        ScenarioRunnerEvent event = new ScenarioRunnerEventFactory().begin();
        
        jmsSender.onEvent(event);
        
        listener.shutdownWhenEmpty();
        
        Assert.assertTrue(TestUtils.checkEvents(new Event[] { event }, repository));
    }    
}
