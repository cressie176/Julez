package uk.co.acuminous.julez.executor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.util.JulezSugar;

public class ConcurrentScenarioExecutor implements ScenarioExecutor {

    private ThreadPoolExecutor executor;
    private int workQueueSize = Integer.MAX_VALUE;
    private int numThreads = 1;
    private long terminationTimeout = Integer.MAX_VALUE;

    public ConcurrentScenarioExecutor allocate(int threads, JulezSugar units) {
        this.numThreads = threads;
        return this;
    }
    
    public ConcurrentScenarioExecutor limitWorkQueueTo(int workQueueSize, JulezSugar units) {
        this.workQueueSize = workQueueSize;
        return this;
    }
    
    public ConcurrentScenarioExecutor awaitTerminationFor(int value, TimeUnit timeUnit) {
        this.terminationTimeout = MILLISECONDS.convert(value, timeUnit);
        return this;        
    }
    
    public ConcurrentScenarioExecutor initialise() {
        executor = new ThreadPoolExecutor(numThreads, numThreads, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return this;
    }         
    
    @Override
    public void execute(Scenario scenario) {    
        if (executor == null) {
            initialise();
        }
        executor.execute(scenario);        
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(terminationTimeout, MILLISECONDS);
    }    
    
    @Override
    public void shutdown() {
        executor.shutdownNow();
    }
}
