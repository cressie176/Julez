package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.EventRecorder;
import uk.co.acuminous.julez.test.InvocationCountingScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import uk.co.acuminous.julez.test.TestUtils;

public class ConcurrentScenarioRunnerTest {
    
    @Test
    public void runsScenarios() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));                
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(10, scenario.counter.get());        
    }
    
    @Test    
    public void timesOutWhenScenariosTakeTooLong() {                       
        Scenarios scenarios = TestUtils.getScenarios(new SleepingScenario(), 10);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).timeOutAfter(5, SECONDS);
        runner.run();
        
        assertTrue("Runner did not timeout", scenarios.available() == 0);
    }  
    
    @Test    
    public void defersStartUntilAGivenTime() {
        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario();        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 1);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).waitUntil(desiredStartTime);
        runner.run();
        
        assertTrue("Runner did not defer start", !scenario.actualStartTime.isBefore(desiredStartTime));
    }    
    
    @Test
    public void raisesBeginEvent() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        

        EventRecorder eventRecorder = new EventRecorder();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
        runner.registerEventHandler(eventRecorder);
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(ScenarioRunnerEvent.BEGIN, eventRecorder.events.get(0).getType());
    }   

    @Test
    public void raisesBeginEventOnOrAfterDeferedStart() {
        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        EventRecorder eventRecorder = new EventRecorder();        
        
        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario();        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 1);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
        runner.queue(scenarios).waitUntil(desiredStartTime);
        runner.registerEventHandler(eventRecorder);                
        runner.run();
        
        assertTrue(eventRecorder.events.get(0).getTimestamp() >= desiredStartTime.getMillis());
    }       
    
    @Test
    public void raisesEndEvent() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);        

        EventRecorder eventRecorder = new EventRecorder();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
        runner.registerEventHandler(eventRecorder);
        runner.queue(scenarios);        
        runner.run();
        
        assertEquals(ScenarioRunnerEvent.END, eventRecorder.events.get(1).getType());
    }
    
    class StartTimeCapturingScenario extends BaseScenario { 

        DateTime actualStartTime;                
        
        @Override public void run() {
            actualStartTime = new DateTime();
        }
    }
}
