package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.co.acuminous.julez.util.JulezSugar.SCENARIOS;
import static uk.co.acuminous.julez.util.JulezSugar.THREADS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
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
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(10, SCENARIOS);                                                                     
        
        new ConcurrentScenarioRunner().queue(scenarios).start();
        
        assertEquals(10, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
        assertEquals(10, repository.count(Event.TYPE, ScenarioEvent.END));
    }
    
    @Test    
    public void doesntBeginNewScenariosAfterRunTimeIsExceeded() {
        
        Scenario scenario = new SleepingScenario(700, MILLISECONDS).register(repository);       
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(10, SCENARIOS);                                                                     
        
        new ConcurrentScenarioRunner().queue(scenarios).runFor(2, SECONDS).start();
        
        assertEquals(3, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
    }
    
    @Test    
    public void interuptsInFlightScenariosWhenRunTimeIsExceeded() {
        
        Scenario scenario = new SleepingScenario(700, MILLISECONDS).register(repository);       
        
        ScenarioSource scenarios = new ScenarioRepeater(scenario);                                                                     
        
        new ConcurrentScenarioRunner().queue(scenarios).runFor(1, SECONDS).start();
        
        assertEquals(1, repository.count(Event.TYPE, ScenarioEvent.ERROR));
    }
    
    @Test    
    public void defersStartUntilAGivenTime() {
        
        long desiredStartTime = new DateTime().plusMillis(500).getMillis();       

        Scenario scenario = new NoOpScenario().register(repository);
        
        ScenarioSource scenarios = new ScenarioHopper(scenario);        
        
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).start();
        
        assertTrue("Runner did not defer start", repository.first().getTimestamp() >= desiredStartTime);
        assertTrue("Runner deferred start by too long", repository.first().getTimestamp() < desiredStartTime + 200);
    } 
    
    @Test    
    public void considersDeferredStartWhenDeterminingWhetherTheGivenRunTimeExceeded() {
        
        long desiredStartTime = new DateTime().plusMillis(500).getMillis();       

        Scenario scenario = new SleepingScenario(700, MILLISECONDS).register(repository);
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(10, SCENARIOS);                                                                     
                
        new ConcurrentScenarioRunner().queue(scenarios).waitUntil(desiredStartTime).runFor(2, SECONDS).start();        
        
        assertEquals(3, repository.count(Event.TYPE, ScenarioEvent.BEGIN));
    }    
    
    @Test
    public void raisesBeginEvent() {
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(new NoOpScenario())).to(10, SCENARIOS);                                                                     
                
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).start();
        
        assertEquals(ScenarioRunnerEvent.BEGIN, repository.first().getType());
    }   

    @Test
    public void raisesBeginEventOnOrAfterDeferedStart() {
        long desiredStartTime = new DateTime().plusSeconds(1).getMillis();       
        
        Scenario scenario = new NoOpScenario().register(repository);
        
        ScenarioSource scenarios = new ScenarioHopper(scenario);        
        
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).waitUntil(desiredStartTime).start();
        
        assertTrue(repository.first().getTimestamp() >= desiredStartTime);
    }       
    
    @Test
    public void raisesEndEvent() {
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(new NoOpScenario())).to(10, SCENARIOS);        
        
        new ConcurrentScenarioRunner().register(repository).queue(scenarios).start();
        
        assertEquals(ScenarioRunnerEvent.END, repository.last().getType());
    }
    
    @Test
    public void canBeConfiguredForMultipleThreads() {
        ThreadCountingScenario scenario = new ThreadCountingScenario();
        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(1000, SCENARIOS);        
        
        new ConcurrentScenarioRunner().queue(scenarios).allocate(10, THREADS).start();
        
        assertEquals(10, scenario.count());
    }

    @Test
    public void stopAllowsInflightScenariosToComplete() {

        FanOutPipe handlers = new FanOutPipe();
        Scenario scenario = new SleepingScenario(700, MILLISECONDS).register(handlers);        
        ScenarioSource scenarios = new SizeLimiter().limit(new ScenarioRepeater(scenario)).to(10, SCENARIOS);                                                                                     
        
        final ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();        
                
        EventHandler shutdownInvoker = new EventHandler() {            
            AtomicInteger counter = new AtomicInteger();            
            @Override
            public void onEvent(Event event) {
                if (ScenarioEvent.BEGIN.equals(event.getType())) {
                    if (counter.incrementAndGet() == 4) {
                        runner.stop(1, SECONDS);
                    }
                }
            }
        };
        
        handlers.registerAll(shutdownInvoker, repository);

        runner.allocate(4, THREADS).queue(scenarios).start();
        
        assertEquals(4, repository.count(Event.TYPE, ScenarioEvent.END));
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
