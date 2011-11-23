package uk.co.acuminous.julez.event.source;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JmsTestUtils;
import uk.co.acuminous.julez.event.handler.EventRecorder;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.source.JmsEventSource;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;

public class JmsEventSourceTest {

    private QueueConnectionFactory connectionFactory;
    private JmsEventSource listener;
    private EventRecorder eventRecorder;
    private JmsEventHandler jmsSender;
    
    @Before
    public void init() {
        JmsTestUtils.createBroker(); 
        
        connectionFactory = JmsTestUtils.getConnectionFactory();
        eventRecorder = new EventRecorder();                

        listener = new JmsEventSource(connectionFactory);
        listener.setShutdownDelay(1, SECONDS);
        listener.register(eventRecorder);
        listener.listen();        
        
        jmsSender = new JmsEventHandler(connectionFactory);        
    }
    
    @After
    public void nuke() throws Exception {
        JmsTestUtils.nukeBroker(); 
    }
    
    @Test
    public void scenarioEventsAreRecreated() throws InterruptedException {
        jmsSender.onEvent(new ScenarioEventFactory().pass());
        
        listener.shutdownGracefully();
        
        assertEquals(1, eventRecorder.getEvents().size());
        assertEquals(ScenarioEvent.PASS, eventRecorder.getEvents().get(0).getType());
    }
    
    @Test
    public void scenarioRunnerEventsAreRecreated() throws InterruptedException {
        jmsSender.onEvent(new ScenarioRunnerEventFactory().begin());
        
        listener.shutdownGracefully();
        
        assertEquals(1, eventRecorder.getEvents().size());
        assertEquals(ScenarioRunnerEvent.BEGIN, eventRecorder.getEvents().get(0).getType());        
    }    
}
