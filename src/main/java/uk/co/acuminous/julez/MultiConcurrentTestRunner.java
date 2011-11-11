package uk.co.acuminous.julez;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiConcurrentTestRunner {

    private final CountDownLatch latch;
    private List<ConcurrentTestRunner> runners;

    public MultiConcurrentTestRunner(ConcurrentTestRunner... concurrentTestRunners) {
        runners = Arrays.asList(concurrentTestRunners);
        latch = new CountDownLatch(runners.size());
    }

    public void run() {
        startRunners();
        waitForRunnersToFinish();
    }

    private void startRunners() {
        for (final ConcurrentTestRunner runner : runners) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        runner.run();
                    } finally {
                        latch.countDown();
                    }
                }
            });
            t.start();
        }
    }

    private void waitForRunnersToFinish() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            // meh
        }
    }
}
