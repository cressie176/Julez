package uk.co.acuminous.julez.event;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.EventJmsSender;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.util.JmsHelper;

import com.google.gson.Gson;

public class JmsSenderTest {

    private QueueConnectionFactory connectionFactory;
    private EventJmsSender jmsSender;
    private ScenarioEventFactory scenarioEventFactory;    

    @Before
    public void init() throws Exception {                
        TestUtils.createBroker();
        connectionFactory = TestUtils.getConnectionFactory();        
        scenarioEventFactory = new ScenarioEventFactory("");                
        jmsSender = new EventJmsSender(connectionFactory);
    }
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker();
    }
    
    @Test
    public void failuresAreWrittenToTheResultsQueue() throws JMSException, InterruptedException {        
        jmsSender.onEvent(scenarioEventFactory.fail());                
        assertScenarioEvent(dequeue(), ScenarioEvent.FAIL);
    }
        
    @Test
    public void passesAreWrittenToTheResultsQueue() throws Exception {        
        jmsSender.onEvent(scenarioEventFactory.pass());                
        assertScenarioEvent(dequeue(), ScenarioEvent.PASS);     
    }

    private ScenarioEvent dequeue() throws JMSException {
        List<TextMessage> messages = JmsHelper.browseMessages(connectionFactory, EventJmsSender.DEFAULT_QUEUE_NAME);
        return new Gson().fromJson(messages.get(0).getText(), ScenarioEvent.class);
    }     
    
    private void assertScenarioEvent(ScenarioEvent event, String eventType) {
        assertEquals(eventType, event.getType());
    }      
}
