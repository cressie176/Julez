package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunnerTest {
    
    private ScenarioRunnerEventFactory scenarioRunnerEventFactory;
    private ScenarioEventFactory scenarioEventFactory;

    @Before
    public void init() {        
        String correlationId = UUID.randomUUID().toString();
        scenarioRunnerEventFactory = new ScenarioRunnerEventFactory(correlationId);        
        scenarioEventFactory = new ScenarioEventFactory(correlationId);        
    }
    
    @Test
    public void runsScenarios() {
        NoopScenario scenario = new NoopScenario(scenarioEventFactory);
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));                
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(10, scenario.counter);        
    }
    
    @Test    
    public void timesOutWhenScenariosTakeTooLong() {                       
        Scenarios scenarios = TestUtils.getScenarios(new SleepingScenario(scenarioEventFactory), 10);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).timeOutAfter(5, SECONDS);
        runner.run();
        
        assertTrue("Runner did not timeout", scenarios.available() == 0);
    }  
    
    @Test    
    public void defersStartUntilAGivenTime() {
        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario(scenarioEventFactory);        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 1);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).waitUntil(desiredStartTime);
        runner.run();
        
        assertTrue("Runner did not defer start", !scenario.actualStartTime.isBefore(desiredStartTime));
    }    
    
    @Test
    public void raisesBeginEvent() {
        NoopScenario scenario = new NoopScenario(scenarioEventFactory);
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        

        EventRecorder eventRecorder = new EventRecorder();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));
        runner.registerEventHandler(eventRecorder);
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(ScenarioRunnerEvent.BEGIN, eventRecorder.events.get(0).getType());
    }   

    @Test
    public void raisesBeginEventFiresOnOrAfterDeferedStart() {
        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        EventRecorder eventRecorder = new EventRecorder();        
        
        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario(scenarioEventFactory);        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 1);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).waitUntil(desiredStartTime);
        runner.registerEventHandler(eventRecorder);                
        runner.run();
        
        assertTrue(eventRecorder.events.get(0).getTimestamp() >= desiredStartTime.getMillis());
    }       
    
    @Test
    public void raisesEndEvent() {
        NoopScenario scenario = new NoopScenario(scenarioEventFactory);
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        

        EventRecorder eventRecorder = new EventRecorder();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenarioRunnerEventFactory).usingExecutor(Executors.newFixedThreadPool(1));
        runner.registerEventHandler(eventRecorder);
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(ScenarioRunnerEvent.END, eventRecorder.events.get(1).getType());
    }
    
    class NoopScenario extends BaseScenario {

        int counter;        
                
        public NoopScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }
        
        @Override public void run() {
            counter++;
        }
    }
    
    class SleepingScenario extends BaseScenario {
        
        public SleepingScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }

        @Override public void run() {
            ConcurrencyUtils.sleep(1, SECONDS);
        }
    }
    
    class StartTimeCapturingScenario extends BaseScenario { 

        DateTime actualStartTime;                
        
        public StartTimeCapturingScenario(ScenarioEventFactory eventFactory) {
            super(eventFactory);
        }
        
        @Override public void run() {
            actualStartTime = new DateTime();
        }
    }
    
    class EventRecorder implements EventHandler {

        List<Event> events = new ArrayList<Event>();
        
        @Override
        public void onEvent(Event event) {
            events.add(event);            
        }        
    }
}
