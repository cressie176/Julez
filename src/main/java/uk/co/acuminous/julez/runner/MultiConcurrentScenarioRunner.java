package uk.co.acuminous.julez.runner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class MultiConcurrentScenarioRunner extends BaseScenarioRunner {

    private final CountDownLatch latch;
    private List<ScenarioRunner> runners;

    public MultiConcurrentScenarioRunner(ScenarioRunner... concurrentTestRunners) {
        runners = Arrays.asList(concurrentTestRunners);
        latch = new CountDownLatch(runners.size());
    }

    public void run() {
        
        begin();
        
        for (final ScenarioRunner runner : runners) {
            
            Runnable r = new Runnable() {
                @Override public void run() {
                    try {
                        runner.run();
                    } finally {
                        latch.countDown();
                    }
                }
            };
            
            ConcurrencyUtils.start(r);
        }
        
        ConcurrencyUtils.await(latch);   
        
        end();
    }
}
