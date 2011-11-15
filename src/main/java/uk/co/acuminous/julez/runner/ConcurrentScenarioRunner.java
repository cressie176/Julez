package uk.co.acuminous.julez.runner;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioListener;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class ConcurrentScenarioRunner implements ScenarioRunner, ScenarioListener {
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private Deque<Scenario> scenarios;
    private long timeout = 365 * 24 * 60 * 60 * 1000;
    private AtomicInteger completedCounter = new AtomicInteger();
    private DateTime startTime = new DateTime();    
    private long started;
    
    public ConcurrentScenarioRunner queue(Deque<Scenario> scenarios) {
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
    
    public ConcurrentScenarioRunner blockUntil(DateTime startTime) {        
        this.startTime = startTime;
        return this;
    }    
    
    @Override
    public void run() {
        
        ConcurrencyUtils.sleep(startTime.getMillis() - new DateTime().getMillis(), MILLISECONDS);
        
        started = System.currentTimeMillis();        
        
        try {
        	while(!scenarios.isEmpty()) {
                Scenario scenario = scenarios.poll();
                scenario.registerListener(this);
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

    @Override
    public int throughput() {
        double completed = completedCounter.get(); 
        double duration = System.currentTimeMillis() - started;        
        return duration > 0 ? (int) Math.round(completed * 1000 / duration) : 0;
    }

    @Override
    public void onComplete() {
        completedCounter.incrementAndGet();        
    }
}
