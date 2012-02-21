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
    private int workQueueSize = Integer.MAX_VALUE;
    private int numThreads = 1;

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

    public ConcurrentScenarioRunner allocate(int threads, JulezSugar units) {
        this.numThreads = threads;
        return this;
    }
    
    public ConcurrentScenarioRunner limitWorkQueueTo(int workQueueSize, JulezSugar units) {
        this.workQueueSize = workQueueSize;
        return this;
    }    
    
    protected ThreadPoolExecutor getDefaultExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numThreads, numThreads, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
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
        executor = executor == null ? getDefaultExecutor() : executor;

        ConcurrencyUtils.sleep((startTime - System.currentTimeMillis()), MILLISECONDS);
        stopTime = System.currentTimeMillis() + timeout;
        handler.onEvent(eventFactory.begin());        
    }

    private void run() {
        boolean keepRunning = shouldKeepRunning();
        while (keepRunning) {
            Scenario scenario = scenarios.next();
            keepRunning = scenario != null && shouldKeepRunning();
            if (keepRunning) {
                executor.execute(scenario);                
            }
        }
    }

    private boolean shouldKeepRunning() {
        return !executor.isShutdown() && (stopTime > System.currentTimeMillis());
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
    
    public ConcurrentScenarioRunner register(EventHandler handler) {
    	super.register(handler);
    	return this;
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("ConcurrentScenarioRunner is being replace. Not going to implement this method");
        
    }
}
