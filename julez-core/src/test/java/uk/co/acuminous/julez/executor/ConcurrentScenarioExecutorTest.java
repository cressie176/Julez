package uk.co.acuminous.julez.executor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIO;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.filter.EventTypeFilter;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioEventFactory;
import uk.co.acuminous.julez.scenario.instruction.SleepScenario;
import uk.co.acuminous.julez.test.TestEventRepository;


public class ConcurrentScenarioExecutorTest {

    @Test
    public void executesScenariosConcurrently() throws InterruptedException {
      
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor();
        executor.allocate(2, THREADS);
        
        SleepScenario scenario = new SleepScenario().sleepFor(300, MILLISECONDS);
        
        TestEventRepository repo = new TestEventRepository();
        scenario.register(repo);
        
        executor.execute(scenario);
        executor.execute(scenario);             
        executor.awaitTermination();
        
        assertEquals(repo.get(0).getType(), ScenarioEvent.BEGIN);
        assertEquals(repo.get(1).getType(), ScenarioEvent.BEGIN);
    }
    
    @Test
    public void awaitsTerminationForTheSpecifiedDuraiton() throws InterruptedException {
      
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor();
        executor.awaitTerminationFor(500, MILLISECONDS);
        
        SleepScenario scenario = new SleepScenario().sleepFor(300, MILLISECONDS);
        
        TestEventRepository repo = new TestEventRepository();
        scenario.register(repo);
        
        executor.execute(scenario);
        executor.awaitTermination();
        
        assertFalse("The executor did not wait", repo.list(new EventTypeFilter(ScenarioEvent.END)).isEmpty());
    }    
    
    @Test
    public void awaitsTerminationForNoMoreThanSpecifiedDuraiton() throws InterruptedException {
      
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor();
        executor.awaitTerminationFor(100, MILLISECONDS);
        
        SleepScenario scenario = new SleepScenario().sleepFor(1, SECONDS);
        
        TestEventRepository repo = new TestEventRepository();
        scenario.register(repo);
        
        executor.execute(scenario);
        executor.awaitTermination();
        
        assertTrue("The executor did not abort", repo.list(new EventTypeFilter(ScenarioEvent.END)).isEmpty());
    }    
    
    
    @Test
    public void awaitsTerminationForEverByDefault() throws InterruptedException {
      
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor();
        
        SleepScenario scenario = new SleepScenario().sleepFor(2, SECONDS);
        
        TestEventRepository repo = new TestEventRepository();
        scenario.register(repo);
        
        executor.execute(scenario);
        executor.awaitTermination();
        
        assertFalse("The executor did not wait", repo.list(new EventTypeFilter(ScenarioEvent.END)).isEmpty());
    }     
    
    @Test
    public void regulatesThroughputUsingQueueManagerThreadWhenWorkQueueLimitReached() throws InterruptedException {
        
        ConcurrentScenarioExecutor executor = new ConcurrentScenarioExecutor();
        executor.allocate(1, THREADS);
        executor.limitWorkQueueTo(1, SCENARIO);
        
        Scenario scenarioA = new SleepScenario().sleepFor(500, MILLISECONDS).useEventFactory(getScenarioEventFactory("A"));
        Scenario scenarioB = new SleepScenario().sleepFor(500, MILLISECONDS).useEventFactory(getScenarioEventFactory("B"));
        Scenario scenarioC = new SleepScenario().sleepFor(500, MILLISECONDS).useEventFactory(getScenarioEventFactory("C"));
                
        TestEventRepository repo = new TestEventRepository();
        scenarioA.register(repo);
        scenarioB.register(repo);
        scenarioC.register(repo);
                
        executor.execute(scenarioA); // Gets dequeued by the worker thread
        executor.execute(scenarioB); // Blocks the work queue
        executor.execute(scenarioC); // Gets dequeued by the queue manager thread        
        executor.awaitTermination();
        
        Event scenarioAEndEvent = new EventTypeFilter(ScenarioEvent.END).applyTo(new EventDataFilter("SCENARIO_ID", "A").applyTo(repo)).get(0);        
        Event scenarioCBeginEvent = new EventTypeFilter(ScenarioEvent.BEGIN).applyTo(new EventDataFilter("SCENARIO_ID", "C").applyTo(repo)).get(0);
        
        assertTrue("Scenario C was not executed by the queue manager thread", scenarioCBeginEvent.getTimestamp() < scenarioAEndEvent.getTimestamp());
    }

    private ScenarioEventFactory getScenarioEventFactory(String scenarioId) {
        Map<String, String> eventData = new HashMap<String, String>();
        eventData.put("SCENARIO_ID", scenarioId);        
        return new ScenarioEventFactory(eventData);
    }
    
}
