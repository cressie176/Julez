package uk.co.acuminous.julez.scenario;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Deque;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;

import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunnerTest {

    public void runsAQueueFullOfOfScenarios() {
    	Scenario counter = new CountingScenario();
    	n = 0;
		Deque<Scenario> scenarios = TestUtils.getScenarios(counter, 10);        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios);
        runner.run();
        assertEquals(10, n);
        assertEquals(0, scenarios.size());        
    }

// is this really a primary use case?     
//    public void runsAGivenNumberOfScenarios() {
//    	Deque<Scenario> scenarios = TestUtils.getScenarios(new NoopScenario(), 10);        
//        ScenarioRunner runner = singleThreadedRunner().queue(5, scenarios);
//        runner.run();
//        assertEquals(5, scenarios.size());        
//    }
    
    public void timesOutWhenScenariosTakeTooLong() {
        
    	Deque<Scenario> scenarios = TestUtils.getScenarios(new SleepingScenario(), 10);        
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios).timeOutAfter(5, SECONDS);
        runner.run();
        
        assertTrue("Runner did not timeout", scenarios.size() == 0);
    }  
    
    
    public void calculatesThroughput() {
        
    	Deque<Scenario> scenarios = TestUtils.getScenarios(new SleepingScenario(), 5);        
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios);
        runner.run();
        
        assertEquals(1, runner.throughput());
    }     
    
    public void calculatesThroughputWithMultiThreadedExecutor() {

    	Deque<Scenario> scenarios = TestUtils.getScenarios(new SleepingScenario(), 25);        
        
        ScenarioRunner runner = twoThreadedRunner().queue(scenarios);
        runner.run();

        assertEquals(2, runner.throughput());
    } 
    
    public void defersStartUntilAGivenTime() {

        DateTime now = new DateTime();
        DateTime desiredStartTime = now.plusSeconds(5);       

        Deque<Scenario> scenarios = TestUtils.getScenarios(new StartTimeCapturingScenario(), 1);        
        
        ScenarioRunner runner = singleThreadedRunner().queue(scenarios).blockUntil(desiredStartTime);
        runner.run();
        
        assertTrue("Runner did not defer start", !desiredStartTime.isBefore(new StartTimeCapturingScenario().actualStartTime));
    }    
    
    private ConcurrentScenarioRunner singleThreadedRunner() {
        return new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(1));
    }
    

    private ConcurrentScenarioRunner twoThreadedRunner() {
        return new ConcurrentScenarioRunner().usingExecutor(Executors.newFixedThreadPool(2));
    }    
    
    class NoopScenario extends BaseScenario {        
        @Override public void run() {
            notifyComplete();
        }
    }
    
	public static int n = 0;
    class CountingScenario extends BaseScenario {
        @Override public void run() {
        	++n;
            notifyComplete();
        }
    }
    
    class SleepingScenario extends BaseScenario {        
        @Override public void run() {
            ConcurrencyUtils.sleep(1, SECONDS);
            notifyComplete();                
        }
    }
    
    class StartTimeCapturingScenario extends BaseScenario { 
        DateTime actualStartTime;        
        @Override public void run() {
            actualStartTime = new DateTime();
            notifyComplete();                
        }
    }     
}
