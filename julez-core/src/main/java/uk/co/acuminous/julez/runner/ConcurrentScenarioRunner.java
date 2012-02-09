package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;
import uk.co.acuminous.julez.util.JulezSugar;

public class ConcurrentScenarioRunner extends BaseScenarioRunner {

    private ThreadPoolExecutor executor;
    private ScenarioSource scenarios;
    private long timeout = 365 * 24 * 60 * 60 * 1000;
    private long startTime = System.currentTimeMillis();
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();
    private long stopTime;

    public ConcurrentScenarioRunner queue(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;
    }

    public ConcurrentScenarioRunner runFor(long value, TimeUnit timeUnit) {
        this.timeout = MILLISECONDS.convert(value, timeUnit);
        return this;
    }

    public ConcurrentScenarioRunner useExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
        return this;
    }

    public ConcurrentScenarioRunner allocate(int clients, JulezSugar units) {
        this.executor = getExecutor(clients);
        return this;
    }
    
    protected ThreadPoolExecutor getExecutor(int numThreads) {
        return new ThreadPoolExecutor(numThreads, numThreads, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }    

    public ConcurrentScenarioRunner useEventFactory(ScenarioRunnerEventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }

    public ConcurrentScenarioRunner waitUntil(long startTime) {
        this.startTime = startTime;
        return this;
    }

    @Override
    public void start() {
        prepare(); 
        run();
        shutdown();
    }

    private void prepare() {
        executor = executor == null ? getExecutor(1) : executor;
        ConcurrencyUtils.sleep((startTime - System.currentTimeMillis()), MILLISECONDS);
        stopTime = System.currentTimeMillis() + timeout;
        handler.onEvent(eventFactory.begin());        
    }

    private void run() {                
        Scenario scenario = scenarios.next();
        while (!executor.isShutdown() && scenario != null && (stopTime > System.currentTimeMillis())) {
            executor.execute(scenario);
            scenario = scenarios.next();
        }
    }

    private void shutdown() {
        try {
            executor.shutdown();
            executor.awaitTermination(stopTime - System.currentTimeMillis(), MILLISECONDS);
        } catch (InterruptedException e) {
            // Meh
        } finally {
            executor.shutdownNow();
        }
        handler.onEvent(eventFactory.end());        
    }
    
    @Override
    public void stop(long timeout, TimeUnit units) {
        try {
            executor.getQueue().clear();            
            executor.shutdown();
            executor.awaitTermination(timeout, units);
        } catch (InterruptedException e) {
            // Meh
        } finally {
            executor.shutdownNow();
        }
        handler.onEvent(eventFactory.end());        
    }    
    
    public ConcurrentScenarioRunner register(EventHandler handler) {
    	super.register(handler);
    	return this;
    }
}
