package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.limiter.SizeLimiter;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.scenario.source.ScenarioRepeater;
import uk.co.acuminous.julez.test.NoOpScenario;
import uk.co.acuminous.julez.test.SleepingScenario;
import uk.co.acuminous.julez.test.TestEventRepository;

public class ConcurrentScenarioRunnerTest {
    
    private TestEventRepository repository;

    @Before
    public void init() {
        repository = new TestEventRepository();        
    }
    
    @Test
    public void runsScenarios() {        
        
        Scenario scenario = new NoOpScenario().register(repository);
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(10, SCENARIOS).to(new ScenarioRepeater(scenario));                                                                     
        
        new ConcurrentScenarioRunner().queue(scenarios).go();
        
        assertEquals(10, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(10, repository.count(Event.TYPE, ScenarioEvent.END));
    }
    
    @Test    
    public void stopsWhenScenariosTakeTooLong() {
        
        Scenario scenario = new SleepingScenario(2, SECONDS).register(repository);       
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(10, SCENARIOS).to(new ScenarioRepeater(scenario));                                                                     
        
        new ConcurrentScenarioRunner().queue(scenarios).runFor(3, SECONDS).go();
        
        assertEquals(2, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(1, repository.count(Event.TYPE, ScenarioEvent.END));
    }
    
    @Test    
    public void defersStartUntilAGivenTime() {
        
        long desiredStartTime = new DateTime().plusSeconds(1).getMillis();       

        Scenario scenario = new NoOpScenario().register(repository);
        
        ScenarioSource scenarios = new ScenarioHopper(scenario);        
        
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).go();
        
        assertTrue("Runner did not defer start", repository.first().getTimestamp() >= desiredStartTime);
    } 
    
    @Test    
    public void stopsWhenScenariosTakeTooLongFromTheSpecifiedStartTime() {
        
        long desiredStartTime = new DateTime().plusSeconds(5).getMillis();       

        Scenario scenario = new SleepingScenario(4, SECONDS).register(repository);
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(3, SCENARIOS).to(new ScenarioRepeater(scenario));                                                                     
                
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).runFor(5, SECONDS).go();
        
        assertEquals(2, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(1, repository.count(Event.TYPE, ScenarioEvent.END));
    }    
    
    @Test
    public void raisesBeginEvent() {
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(10, SCENARIOS).to(new ScenarioRepeater(new NoOpScenario()));                                                                     
                
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).go();
        
        assertEquals(ScenarioRunnerEvent.BEGIN, repository.first().getType());
    }   

    @Test
    public void raisesBeginEventOnOrAfterDeferedStart() {
        long desiredStartTime = new DateTime().plusSeconds(5).getMillis();       
        
        Scenario scenario = new NoOpScenario().register(repository);
        
        ScenarioSource scenarios = new ScenarioHopper(scenario);        
        
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).waitUntil(desiredStartTime).go();
        
        assertTrue(repository.first().getTimestamp() >= desiredStartTime);
    }       
    
    @Test
    public void raisesEndEvent() {
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(10, SCENARIOS).to(new ScenarioRepeater(new NoOpScenario()));        
        
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).go();
        
        assertEquals(ScenarioRunnerEvent.END, repository.last().getType());
    }
    
    @Test
    public void canBeConfiguredForMultipleThreads() {
        ThreadCountingScenario scenario = new ThreadCountingScenario();
        
        ScenarioSource scenarios = new SizeLimiter().applyLimitOf(1000, SCENARIOS).to(new ScenarioRepeater(scenario));        
        
        new ConcurrentScenarioRunner().queue(scenarios).allocate(10, THREADS).go();
        
        assertEquals(10, scenario.count());
    }
    
    class ThreadCountingScenario extends BaseScenario {

        private Set<Thread> threads = Collections.synchronizedSet(new HashSet<Thread>());
        
        @Override
        public void run() {
            handler.onEvent(eventFactory.begin());
            threads.add(Thread.currentThread());
            handler.onEvent(eventFactory.end());        
        }
        
        public int count() {
            return threads.size();
        }

    }    
}
