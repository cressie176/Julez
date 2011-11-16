package uk.co.acuminous.julez.scenario.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.event.ScenarioEvent;
import uk.co.acuminous.julez.scenario.event.ScenarioEventHandler;
import uk.co.acuminous.julez.scenario.event.ScenarioEventJmsListener;
import uk.co.acuminous.julez.scenario.event.ScenarioEventJmsSender;
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
        listener.registerListeners(recorder);
        listener.listen();                        
                
        ScenarioEventJmsSender scenarioEventJmsSender = new ScenarioEventJmsSender(connectionFactory);
        scenarioEventJmsSender.onScenarioEvent(ScenarioEvent.pass());
        
        listener.shutdownGracefully();
        
        assertEquals(1, recorder.events.size());
    }

    class ScenarioEventRecorder implements ScenarioEventHandler {

        List<ScenarioEvent> events = new ArrayList<ScenarioEvent>();
        
        @Override
        public void onScenarioEvent(ScenarioEvent event) {
            events.add(event);
        }
        
    }
}
