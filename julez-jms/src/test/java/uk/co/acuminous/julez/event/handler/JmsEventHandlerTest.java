package uk.co.acuminous.julez.event.handler;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.JmsTestUtils;
import uk.co.acuminous.julez.event.handler.JmsEventHandler;
import uk.co.acuminous.julez.event.marshaller.JsonEventMarshaller;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerEventFactory;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.util.JmsHelper;

import com.google.gson.Gson;

public class JmsEventHandlerTest {

    private QueueConnectionFactory connectionFactory;

    @Before
    public void init() throws Exception {                
        JmsTestUtils.createBroker();
        connectionFactory = JmsTestUtils.getConnectionFactory();        
    }
    
    @After
    public void nuke() throws Exception {
        JmsTestUtils.nukeBroker();
    }
    
    @Test
    public void scenarioEventsAreWrittenToTheQueue() throws JMSException, InterruptedException {      
        JmsEventHandler jmsSender = new JmsEventHandler(connectionFactory, new JsonEventMarshaller());        
        jmsSender.onEvent(new ScenarioEventFactory().fail());                
        assertScenarioEvent(dequeue(), ScenarioEvent.FAIL);
    }
        
    @Test
    public void scenarioRunnerEventsAreWrittenToTheQueue() throws Exception { 
        JmsEventHandler jmsSender = new JmsEventHandler(connectionFactory, new JsonEventMarshaller());        
        jmsSender.onEvent(new ScenarioRunnerEventFactory().begin());                
        assertScenarioEvent(dequeue(), ScenarioRunnerEvent.BEGIN);     
    }

    private ScenarioEvent dequeue() throws JMSException {
        List<TextMessage> messages = JmsHelper.browseMessages(connectionFactory, JmsEventHandler.DEFAULT_QUEUE_NAME);
        return new Gson().fromJson(messages.get(0).getText(), ScenarioEvent.class);
    }     
    
    private void assertScenarioEvent(ScenarioEvent event, String eventType) {
        assertEquals(eventType, event.getType());
    }      
}
