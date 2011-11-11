package uk.co.acuminous.julez;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CombinationPerformanceTest {

	private static final int MAX_THROUGHPUT = 50;
	private static final int TEST_DURATION = 15;	
	
	@Test
	public void testTheSystemSupportsDifferentScearnios() throws Throwable {		
		
		ConcurrentTestRunner runner1 = new ConcurrentTestRunner(new HelloWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
		ConcurrentTestRunner runner2 = new ConcurrentTestRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, TEST_DURATION);
		
		MultiConcurrentTestRunner multiTestRunner = new MultiConcurrentTestRunner(runner1, runner2);
		multiTestRunner.run();
		
		assertTrue(String.format("Actual throughput: %d scenarios per second", runner1.actualThroughput()), runner1.actualThroughput() >= 20);
		assertTrue(String.format("Actual throughput: %d scenarios per second", runner2.actualThroughput()), runner2.actualThroughput() >= 20);
	}	
		
	class HelloWorldScenario implements Scenario {							
		public void execute() {			
			System.out.println("Hello World");
		}		
	}	
	
	class GoodbyeWorldScenario implements Scenario {							
		public void execute() {			
			System.out.println("Goodbye World");
		}		
	}	
}
