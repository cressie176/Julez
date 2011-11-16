package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunner implements ScenarioRunner {
    
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private Scenarios scenarios;    
    private int numberOfScenarios;        
    private long timeout = 365 * 24 * 60 * 60 * 1000;
    private DateTime startTime = new DateTime();    
    
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
    
    public ConcurrentScenarioRunner waitUntil(DateTime startTime) {        
        this.startTime = startTime;
        return this;
    }    
    
    @Override
    public void run() {
        
        ConcurrencyUtils.sleep(startTime.getMillis() - new DateTime().getMillis(), MILLISECONDS);
                
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
    }
}
