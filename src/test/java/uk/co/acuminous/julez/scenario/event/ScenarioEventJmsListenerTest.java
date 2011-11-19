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
import uk.co.acuminous.julez.event.repository.ScenarioEventJmsListener;
import uk.co.acuminous.julez.event.repository.ScenarioEventJmsSender;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.test.TestUtils;

public class ScenarioEventJmsListenerTest {

    private QueueConnectionFactory connectionFactory;

    @Before
    public void init() throws Exception {
        TestUtils.createBroker();
        
        connectionFactory = TestUtils.getConnectionFactory();                                
    }
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker(); 
    }
    
    @Test
    public void resultsAreAddedToTheRepository() throws InterruptedException {

        ScenarioEventRecorder recorder = new ScenarioEventRecorder();
        
        ScenarioEventJmsListener listener = new ScenarioEventJmsListener(connectionFactory);
        listener.registerEventHandler(recorder);
        listener.listen();                        
                
        ScenarioEventJmsSender scenarioEventJmsSender = new ScenarioEventJmsSender(connectionFactory);
        scenarioEventJmsSender.onEvent(ScenarioEvent.pass());
        
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
