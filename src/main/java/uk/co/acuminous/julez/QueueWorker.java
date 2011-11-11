package uk.co.acuminous.julez;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;

public class QueueWorker implements Runnable {

    private DelayQueue<DelayedScenario> queue;
    private CountDownLatch latch;

    public QueueWorker(DelayQueue<DelayedScenario> queue, CountDownLatch latch) {
        this.queue = queue;
        this.latch = latch;
    }

    public void run() {
        try {
            while (true) {
                try {
                    queue.take().execute();
                } finally {
                    latch.countDown();
                }
            }
        } catch (InterruptedException e) {
            // Meh
        }
    }
}
