package uk.co.acuminous.julez.marshalling.json;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.marshalling.NamespaceBasedEventClassResolver;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class JsonEventMarshallerTest {

    private JsonEventMarshaller marshaller;
    
    @Before
    public void init() {
        marshaller = new JsonEventMarshaller(new NamespaceBasedEventClassResolver());        
    }

    @Test
    public void marshallsAScenarioEvent() {
        ScenarioEvent event = aScenarioEvent();
        assertEquals("{\"#TIMESTAMP\":\"1\",\"#TYPE\":\"Scenario/begin\",\"#ID\":\"id\"}", marshaller.marshall(event));        
    }

    @Test
    public void marshallsAScenarioRunnerEvent() {
        ScenarioRunnerEvent event = aScenarioRunnerEvent();
        assertEquals("{\"#TIMESTAMP\":\"1\",\"#TYPE\":\"ScenarioRunner/end\",\"#ID\":\"id\"}", marshaller.marshall(event));        
    } 
    
    @Test 
    public void unmarshalsAScenarioEvent() {
        Event expected = aScenarioEvent();
        String json = marshaller.marshall(expected);
        assertEquals(expected, marshaller.unmarshall(json));        
    }
        
    @Test 
    public void unmarshalsAScenarioRunnerEvent() {
        Event expected = aScenarioRunnerEvent();
        String json = marshaller.marshall(expected);
        assertEquals(expected, marshaller.unmarshall(json));        
    }    
    
    private ScenarioEvent aScenarioEvent() {
        return new ScenarioEvent("id", 1L, ScenarioEvent.BEGIN);
    }

    private ScenarioRunnerEvent aScenarioRunnerEvent() {
        return new ScenarioRunnerEvent("id", 1L, ScenarioRunnerEvent.END);
    }    
}
