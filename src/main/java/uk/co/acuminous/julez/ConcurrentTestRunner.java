package uk.co.acuminous.julez;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

public class ConcurrentTestRunner {

	private Scenario scenario;
	private int duration;
	private int executionInterval;
	private int plannedExecutions;
	private int numberOfWorkers = 10;
	private final DelayQueue<DelayedScenario> queue = new DelayQueue<DelayedScenario>();
	private CountDownLatch latch;
	private ExecutorService executor;

	public ConcurrentTestRunner(Scenario scenario, int scheduledThroughput, int testDuration) {
		this.scenario = scenario;
		this.duration = testDuration;
		this.plannedExecutions = scheduledThroughput * testDuration;
		this.executionInterval = 1000 / scheduledThroughput;
	}

	public void run() {
		loadTestScenarios();
		executeTestScenarios();
		waitForTestScenariosToFinish();
	}

	private void loadTestScenarios() {
		queue.clear();
		DateTime now = new DateTime();
		for (int i = 0; i < plannedExecutions; i++) {
			queue.add(new DelayedScenario(now.plusMillis(i * executionInterval), scenario));
		}
		latch = new CountDownLatch(queue.size()); 		
	}	
	
	private void executeTestScenarios() {
		executor = Executors.newFixedThreadPool(numberOfWorkers);
		for (int i = 0; i < numberOfWorkers; i++) {
			executor.execute(new QueueWorker(queue, latch));
		}
	}

	private void waitForTestScenariosToFinish() {
		try {
			latch.await(duration, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// Meh
		} finally {		
			executor.shutdownNow();
		}
		
	}
	
	public int numberOfExecutionsRemaining() {
		return queue.size();
	}
	
	public int actualThroughput() {
		return (plannedExecutions - numberOfExecutionsRemaining()) / duration;
	}

	public void useNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}
}
