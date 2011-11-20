package uk.co.acuminous.julez.event.async;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.TestUtils;

public class JmsEventListenerTest {

    private QueueConnectionFactory connectionFactory;
    private ScenarioEventFactory scenarioEventFactory;
    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    private JmsEventListener listener;
    private ScenarioEventRecorder recorder;
    private JmsEventSender scenarioEventJmsSender;
    
    @Before
    public void init() {
        TestUtils.createBroker();        
        connectionFactory = TestUtils.getConnectionFactory();
        scenarioEventFactory = new ScenarioEventFactory();  
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory();
        recorder = new ScenarioEventRecorder();                
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

    class ScenarioEventRecorder implements EventHandler {

        List<Event<?>> events = new ArrayList<Event<?>>();
        
        @Override
        public void onEvent(Event<?> event) {
            events.add(event);
        }
        
    }
}
