package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunner extends BaseScenarioRunner {
    
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private Scenarios scenarios;    
    private int numberOfScenarios;        
    private long timeout = 365 * 24 * 60 * 60 * 1000;
    private long startTime = System.currentTimeMillis();
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();    
    
    public ConcurrentScenarioRunner() {
    }
    
    public ConcurrentScenarioRunner queue(Scenarios scenarios) {
        this.numberOfScenarios = scenarios.available();
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
        this.startTime = startTime;
        return this;
    }    
    
    @Override
    public void run() {
                
        ConcurrencyUtils.sleep(startTime - System.currentTimeMillis(), MILLISECONDS);
        
        raise(eventFactory.begin());
        
        try {
            for (int i = 0; i < numberOfScenarios; i++) {
                Scenario scenario = scenarios.next();
                executor.execute(scenario);
            }
            executor.shutdown();            
            if (!executor.awaitTermination(timeout, MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Meh
        }    
        
        raise(eventFactory.end());
    }
}
