package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;
import uk.co.acuminous.julez.event.handler.EventMonitor;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.InvocationCountingScenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.SleepingScenario;
import uk.co.acuminous.julez.scenario.ThreadCountingScenario;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;

public class ConcurrentScenarioRunnerTest {
    
    @Test
    public void runsScenarios() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 10); 
        
        new ConcurrentScenarioRunner().queue(scenarios).go();
        
        assertEquals(10, scenario.counter.get());        
    }
    
    @Test    
    public void stopsWhenScenariosTakeTooLong() {                       
        ScenarioSource scenarios = new SizedScenarioRepeater(new SleepingScenario(), 10);        
        
        new ConcurrentScenarioRunner().queue(scenarios).runFor(5, SECONDS).go();
        
        assertTrue("Runner did not timeout", scenarios.available() == 0);
    }
    
    @Test    
    public void defersStartUntilAGivenTime() {
        DateTime now = new DateTime();
        long desiredStartTime = now.plusSeconds(5).getMillis();       

        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario();        
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 1);        
        
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).go();
        
        assertTrue("Runner did not defer start", !scenario.actualStartTime.isBefore(desiredStartTime));
    } 
    
    @Test    
    public void stopsWhenScenariosTakeTooLongAfterGivenStartTime() {
        DateTime now = new DateTime();
        long desiredStartTime = now.plusSeconds(5).getMillis();       

        ScenarioSource scenarios = new SizedScenarioRepeater(new SleepingScenario(), 10);        
        
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).runFor(5, SECONDS).go();
        
        assertTrue("Runner did not timeout", scenarios.available() == 0);
    }    
    
    @Test
    public void raisesBeginEvent() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 10);        

        EventMonitor eventRecorder = new EventMonitor();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(eventRecorder);
        runner.queue(scenarios).go();
        
        assertEquals(ScenarioRunnerEvent.BEGIN, eventRecorder.getEvents().get(0).getType());
    }   

    @Test
    public void raisesBeginEventOnOrAfterDeferedStart() {
        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        EventMonitor eventRecorder = new EventMonitor();        
        
        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario();        
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 1);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(eventRecorder);        
        runner.queue(scenarios).waitUntil(desiredStartTime.getMillis()).go();
        
        assertTrue(eventRecorder.getEvents().get(0).getTimestamp() >= desiredStartTime.getMillis());
    }       
    
    @Test
    public void raisesEndEvent() {
        InvocationCountingScenario scenario = new InvocationCountingScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 10);        

        EventMonitor eventRecorder = new EventMonitor();        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(eventRecorder);
        runner.queue(scenarios).go();
        
        assertEquals(ScenarioRunnerEvent.END, eventRecorder.getEvents().get(1).getType());
    }
    
    @Test
    public void canBeConfiguredForMultipleThreads() {
        ThreadCountingScenario scenario = new ThreadCountingScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 1000);        
        
        new ConcurrentScenarioRunner().queue(scenarios).allocate(10, THREADS).go();
        
        assertEquals(10, scenario.count());
    }    
    
    class StartTimeCapturingScenario extends BaseScenario { 

        DateTime actualStartTime;                
        
        @Override public void run() {
            actualStartTime = new DateTime();
        }
    }
}
