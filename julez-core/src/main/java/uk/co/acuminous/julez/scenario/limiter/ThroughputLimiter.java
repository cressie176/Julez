package uk.co.acuminous.julez.scenario.limiter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.util.JulezSugar;

public class ThroughputLimiter implements ScenarioSource {

    private DelayQueue<Delayed> limiter = new DelayQueue<Delayed>();    
    private ScenarioSource scenarios;
    private int numScenarios;
    private long interval;    
    private long frequency;
    private long startTime;
    private AtomicInteger counter = new AtomicInteger();

    public ThroughputLimiter() {
        this.limiter.add(new DelayToken(System.currentTimeMillis()));        
    }
    
    public ThroughputLimiter(ScenarioSource scenarios, int numScenarios, long interval, TimeUnit units) {
        this.scenarios = scenarios;
        this.numScenarios = numScenarios;
        this.interval = MILLISECONDS.convert(interval, units);        
        this.limiter.add(new DelayToken(System.currentTimeMillis()));
    }

    public ThroughputLimiter applyLimitOf(int numScenarios, JulezSugar units) {
        this.numScenarios = numScenarios;        
        return this;
    }

    public ThroughputLimiter perSecond() {
        this.interval = 1000;
        return this;
    }
    
    public ThroughputLimiter perMinute() {
        this.interval = 1000 * 60;
        return this;        
    }
    
    public ThroughputLimiter perHour() {
        this.interval = 1000 * 60 * 60;
        return this;
    }
    
    public ThroughputLimiter to(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;
    }
    
    @Override
    public Scenario next() {
        Scenario scenario = null;
        try {
            limitThroughput();
            scenario = scenarios.next(); 
        } catch (InterruptedException e) {
            // Meh
        }        
        return scenario;
    }

    private void limitThroughput() throws InterruptedException {
        init();       
        limiter.take();
        counter.incrementAndGet();        
        long blockUntil = startTime + (counter.get() *  frequency);
        limiter.add(new DelayToken(blockUntil));
    }

    private void init() {
        if (startTime == 0) {
            frequency = interval / numScenarios;
            startTime = System.currentTimeMillis();            
        }
    }

    private class DelayToken implements Delayed {

        private long blockUntil;
        
        DelayToken(long blockUntil) {
            this.blockUntil = blockUntil;
        }
        
        @Override
        public int compareTo(Delayed other) {
            DelayToken otherToken = (DelayToken) other;
            return new Long(this.blockUntil).compareTo(otherToken.blockUntil);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(blockUntil - System.currentTimeMillis(), MILLISECONDS);
        }
    }
}
