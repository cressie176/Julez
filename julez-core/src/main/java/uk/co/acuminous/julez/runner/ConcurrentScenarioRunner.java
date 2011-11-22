package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunner extends BaseScenarioRunner {
    
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private ScenarioSource scenarios;            
    private long timeout = 365 * 24 * 60 * 60 * 1000;
    private long startTime = System.currentTimeMillis();
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();    
    
    public ConcurrentScenarioRunner() {
    }
    
    public ConcurrentScenarioRunner queue(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;        
    }    
    
    public ConcurrentScenarioRunner timeOutAfter(long value, TimeUnit timeUnit) {
        this.timeout  = MILLISECONDS.convert(value, timeUnit);
        return this;
    }
    
    public ConcurrentScenarioRunner usingExecutor(ExecutorService executor) {
        this.executor.shutdownNow();
        this.executor = executor;
        return this;
    }
    
    public void useEventFactory(ScenarioRunnerEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }
    
    public ConcurrentScenarioRunner waitUntil(long startTime) {        
        this.startTime = startTime + 1000;
        return this;
    }    
    
    @Override
    public void run() {
                
        ConcurrencyUtils.sleep((startTime - System.currentTimeMillis()), MILLISECONDS);
        long stopTime = System.currentTimeMillis() + timeout;
        
        raise(eventFactory.begin());
        
        while ((scenarios.available() > 0) && (stopTime > System.currentTimeMillis())) {
            Scenario scenario = scenarios.next();
            executor.execute(scenario);
        }
        try {
            executor.shutdown();
            executor.awaitTermination(stopTime - System.currentTimeMillis(), MILLISECONDS);
        } catch (InterruptedException e) {
            // Meh
        } finally {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
        }
        
        raise(eventFactory.end());
    }
}
