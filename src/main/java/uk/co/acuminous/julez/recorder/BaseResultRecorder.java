package uk.co.acuminous.julez.recorder;

import java.util.concurrent.CountDownLatch;

import uk.co.acuminous.julez.util.ConcurrencyUtils;

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
        
        Runnable r = new Runnable() {
            @Override public void run() {
                me.shutdownGracefully();
                latch.countDown();
            }
        };
        
        ConcurrencyUtils.await(r, latch, timeout);
    }    
    
}
