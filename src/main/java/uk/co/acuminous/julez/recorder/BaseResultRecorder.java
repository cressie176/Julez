package uk.co.acuminous.julez.recorder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class BaseResultRecorder implements ResultRecorder {
    
    
    @Override
    public void pass() {
        pass("");
    }    
    
    @Override
    public int percentage() {
        int passCount = passCount();
        int failCount = failureCount();        
        int totalCount = passCount + failCount;
        return totalCount > 0 ? (passCount * 100) / totalCount : 0;
    }
    
    @Override
    public synchronized void complete(int timeout) {
        
        final ResultRecorder me = this;
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                me.shutdownGracefully();
                latch.countDown();
            }            
        });
        t.start();
        
        try {
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Meh
        }
    }    

}
