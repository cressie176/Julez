package uk.co.acuminous.julez.event.async;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.EventRecorder;
import uk.co.acuminous.julez.test.TestUtils;

public class JmsEventListenerTest {

    private QueueConnectionFactory connectionFactory;
    private ScenarioEventFactory scenarioEventFactory;
    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    private JmsEventListener listener;
    private EventRecorder recorder;
    private JmsEventSender scenarioEventJmsSender;
    
    @Before
    public void init() {
        TestUtils.createBroker();        
        connectionFactory = TestUtils.getConnectionFactory();
        scenarioEventFactory = new ScenarioEventFactory();  
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();
        recorder = new EventRecorder();                
        initilaiseJmsEventListener();
        
        scenarioEventJmsSender = new JmsEventSender(connectionFactory);        
    }
    
    private void initilaiseJmsEventListener() {
        listener = new JmsEventListener(connectionFactory);
        listener.setShutdownDelay(1, SECONDS);
        listener.registerEventHandler(recorder);
        listener.listen();
    }    
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker(); 
    }
    
    @Test
    public void scenarioEventsAreRecreated() throws InterruptedException {
        scenarioEventJmsSender.onEvent(scenarioEventFactory.pass());
        
        listener.shutdownGracefully();
        
        assertEquals(1, recorder.events.size());
        assertEquals(ScenarioEvent.PASS, recorder.events.get(0).getType());
    }
    
    @Test
    public void scenarioRunnerEventsAreRecreated() throws InterruptedException {
        scenarioEventJmsSender.onEvent(scenarioRunnerEventFactory.begin());
        
        listener.shutdownGracefully();
        
        assertEquals(1, recorder.events.size());
        assertEquals(ScenarioRunnerEvent.BEGIN, recorder.events.get(0).getType());        
    }    
}
