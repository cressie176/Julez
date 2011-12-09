package uk.co.acuminous.julez.runner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.util.ConcurrencyUtils;

public class MultiConcurrentScenarioRunner extends BaseScenarioRunner {

    private final CountDownLatch latch;
    private final List<ScenarioRunner> runners;
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();

    public MultiConcurrentScenarioRunner(ScenarioRunner... concurrentTestRunners) {
        runners = Arrays.asList(concurrentTestRunners);
        latch = new CountDownLatch(runners.size());
    }

    public void usingEventFactory(ScenarioRunnerEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public void go() {

        handler.onEvent(eventFactory.begin());

        for (final ScenarioRunner runner : runners) {

            ConcurrencyUtils.start(new Runnable() {
                @Override
                public void run() {
                    try {
                        runner.go();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        ConcurrencyUtils.await(latch);

        handler.onEvent(eventFactory.end());
    }
    
    @Override
    public MultiConcurrentScenarioRunner register(EventHandler handler) {
        super.register(handler);
        return this;
    }
}
