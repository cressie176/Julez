package uk.co.acuminous.julez.runner;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.Scenarios;

public class ThroughputLimiter implements Scenarios {

    private final Scenarios scenarios;
    private final DelayQueue<Delayed> limiter = new DelayQueue<Delayed>();
    private final long frequency;
    private long startTime;
    private AtomicInteger counter = new AtomicInteger();

    public ThroughputLimiter(Scenarios scenarios, long frequency, TimeUnit unit) {
        this.scenarios = scenarios;
        this.frequency = MILLISECONDS.convert(frequency, unit);
        this.limiter.add(new DelayToken(System.currentTimeMillis()));
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
        initStartTime();       
        limiter.take();
        counter.incrementAndGet();        
        long blockUntil = startTime + (counter.get() *  frequency);
        limiter.add(new DelayToken(blockUntil));
    }

    private void initStartTime() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public int available() {
        return scenarios.available();
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
