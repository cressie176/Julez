package uk.co.acuminous.julez.scenario;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventMonitor;

public class JBehaveEmbedderScenarioTest {

    private List<String> stepRecorder;
    private URL scenarioLocation;
    private EventMonitor eventRecorder;

    @Before
    public void init() {
        stepRecorder = new ArrayList<String>();
        eventRecorder = new EventMonitor();        
        scenarioLocation = codeLocationFromClass(this.getClass());        
    }
    
    @Test
    public void runsJBehaveScenario() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave-scenario-performs-steps.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.run();
        
        assertEquals(3, stepRecorder.size());
        assertEquals("given", stepRecorder.get(0));
        assertEquals("when", stepRecorder.get(1));
        assertEquals("then", stepRecorder.get(2));
    }
    
    @Test
    public void raisesPassEventOnSuccess() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave-scenario-performs-steps.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();
        
        assertEquals(3, eventRecorder.getEvents().size());                
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());        
        assertEquals(ScenarioEvent.PASS, eventRecorder.getEvents().get(1).getType());
        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());
    } 
    
    @Test
    public void raisesErrorEventOnStoryNotFound() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "does-not-exist.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();

        assertEquals(3, eventRecorder.getEvents().size());
        
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());        
        
        Event event = eventRecorder.getEvents().get(1);
        assertEquals(ScenarioEvent.ERROR, event.getType());
        assertEquals("Cannot find story for does-not-exist.txt", event.getData().get("message"));
        
        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());        
    } 
    
    @Test
    public void raisesErrorEventOnStepNotFound() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave-scenario-handles-missing-steps.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();
        
        assertEquals(3, eventRecorder.getEvents().size());        
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());
        
        Event event = eventRecorder.getEvents().get(1);
        assertEquals(ScenarioEvent.ERROR, event.getType());
        assertEquals("Given the step does not exist", event.getData().get("message"));
        
        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());        
    }    
    
    @Test
    public void raisesFailureEventWhenStepFails() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave-scenario-handles-failures.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();
        
        assertEquals(3, eventRecorder.getEvents().size());        
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());
        
        Event event = eventRecorder.getEvents().get(1);
        assertEquals(ScenarioEvent.FAIL, event.getType());
        assertEquals("Assertion Failed", event.getData().get("message"));

        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());        
    }
    
    @Test
    public void raisesErrorEventWhenStepThrowsAnException() {
        
        JBehaveEmbedderScenario scenario = new JBehaveEmbedderScenario(scenarioLocation, "jbehave-scenario-handles-errors.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();
        
        assertEquals(3, eventRecorder.getEvents().size());
        
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());        
        
        Event event = eventRecorder.getEvents().get(1);
        assertEquals(ScenarioEvent.ERROR, event.getType());
        assertEquals("Test Exception", event.getData().get("message"));
        
        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());        
    }    
}
