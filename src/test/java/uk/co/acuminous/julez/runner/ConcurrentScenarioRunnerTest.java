package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunnerTest {
    
    @Test
    public void runsScenarios() {
        NoopScenario scenario = new NoopScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 10);
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios).timeOutAfter(5, SECONDS);
        runner.run();
        
        assertEquals(10, scenario.counter);        
    }
    
    @Test    
    public void timesOutWhenScenariosTakeTooLong() {
                
        Scenarios scenarios = TestUtils.getScenarios(new SleepingScenario(), 10);        
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios).timeOutAfter(5, SECONDS);
        runner.run();
        
        assertTrue("Runner did not timeout", scenarios.available() == 0);
    }  
    
    @Test    
    public void defersStartUntilAGivenTime() {

        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        StartTimeCapturingScenario scenario = new StartTimeCapturingScenario();        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 1);        
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios).waitUntil(desiredStartTime);
        runner.run();
        
        assertTrue("Runner did not defer start", !scenario.actualStartTime.isBefore(desiredStartTime));
    }    
    
    private ConcurrentScenarioRunner singleThreadedRunner() {
        return new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
    }   
    
    class NoopScenario extends BaseScenario {         
        int counter;        
        @Override public void run() {
            counter++;
        }
    }
    
    class SleepingScenario extends BaseScenario {        
        @Override public void run() {
            ConcurrencyUtils.sleep(1, SECONDS);
        }
    }
    
    class StartTimeCapturingScenario extends BaseScenario { 
        DateTime actualStartTime;        
        @Override public void run() {
            actualStartTime = new DateTime();
        }
    }     
}
