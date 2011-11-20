package uk.co.acuminous.julez.scenario.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventJmsListener;
import uk.co.acuminous.julez.event.EventJmsSender;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.TestUtils;

public class ScenarioEventJmsListenerTest {

    private QueueConnectionFactory connectionFactory;
    private ScenarioEventFactory scenarioEventFactory;
    
    @Before
    public void init() {
        TestUtils.createBroker();        
        connectionFactory = TestUtils.getConnectionFactory();
        scenarioEventFactory = new ScenarioEventFactory("");                
    }
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker(); 
    }
    
    @Test
    public void resultsAreAddedToTheRepository() throws InterruptedException {

        ScenarioEventRecorder recorder = new ScenarioEventRecorder();
        
        EventJmsListener listener = new EventJmsListener(connectionFactory);
        listener.registerEventHandler(recorder);
        listener.listen();                        
                
        EventJmsSender scenarioEventJmsSender = new EventJmsSender(connectionFactory);
        scenarioEventJmsSender.onEvent(scenarioEventFactory.pass());
        
        listener.shutdownGracefully();
        
        assertEquals(1, recorder.events.size());
    }

    class ScenarioEventRecorder implements EventHandler {

        List<Event> events = new ArrayList<Event>();
        
        @Override
        public void onEvent(Event event) {
            events.add(event);
        }
        
    }
}
