package uk.co.acuminous.julez.scenario;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventRecorder;

public class JBehaveScenarioTest {

    private List<String> stepRecorder;
    private URL scenarioLocation;
    private EventRecorder eventRecorder;

    @Before
    public void init() {
        stepRecorder = new ArrayList<String>();
        eventRecorder = new EventRecorder();        
        scenarioLocation = codeLocationFromClass(this.getClass());        
    }
    
    @Test
    public void runsJBehaveScenario() {
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "jbehave-scenario-performs-steps.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.run();
        
        assertEquals(3, stepRecorder.size());
        assertEquals("given", stepRecorder.get(0));
        assertEquals("when", stepRecorder.get(1));
        assertEquals("then", stepRecorder.get(2));
    }
    
    @Test
    public void raisesPassEventOnSuccess() {
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "jbehave-scenario-performs-steps.txt", new ScenarioSteps(stepRecorder));        
        
        scenario.register(eventRecorder);
        
        scenario.run();
        
        assertEquals(3, eventRecorder.getEvents().size());                
        assertEquals(ScenarioEvent.BEGIN, eventRecorder.getEvents().get(0).getType());        
        assertEquals(ScenarioEvent.PASS, eventRecorder.getEvents().get(1).getType());
        assertEquals(ScenarioEvent.END, eventRecorder.getEvents().get(2).getType());
    } 
    
    @Test
    public void raisesErrorEventOnStoryNotFound() {
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "does-not-exist.txt", new ScenarioSteps(stepRecorder));        
        
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
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "jbehave-scenario-handles-missing-steps.txt", new ScenarioSteps(stepRecorder));        
        
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
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "jbehave-scenario-handles-failures.txt", new ScenarioSteps(stepRecorder));        
        
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
        
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "jbehave-scenario-handles-errors.txt", new ScenarioSteps(stepRecorder));        
        
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
