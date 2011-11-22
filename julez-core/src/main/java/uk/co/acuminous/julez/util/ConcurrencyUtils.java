package uk.co.acuminous.julez.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConcurrencyUtils {

    public static Thread start(Runnable r) {
        Thread t = new Thread(r);
        t.start();
        return t;
    }
    
    public static void await(Runnable r, CountDownLatch latch, int timeout) {        
        start(r);        
        await(latch, timeout);               
    }
    
    public static void await(Runnable r, CountDownLatch latch) {
        start(r);      
        await(latch);               
    }    

    public static void await(CountDownLatch latch, int timeout) {
        try {
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Meh
        }
    }

    public static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            // Meh
        }
    }

    public static void scheduleInterrupt(long timeout) {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                Thread.currentThread().interrupt();                
            }            
        }, timeout);
    }

    public static void sleep(long value, TimeUnit timeUnit) {
        try {
            Thread.sleep(Math.max(TimeUnit.MILLISECONDS.convert(value, timeUnit), 0));
        } catch (InterruptedException e) {
            // Meh
        }        
    }        
}
