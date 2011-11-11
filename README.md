# Julez

An extremely lightweight framework for running simple performance tests via junit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave etc), 
then use Julez to run the scenario repeatedly from multiple threads at a capped rate. e.g.

	@Test
	public void testTheSystemSupportsAtLeast80HelloWorldScenariosPerSecond() {
	
	    ConcurrentTestRunner runner = new ConcurrentTestRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
	    runner.run();
	    int throughput = runner.actualThroughput();
	    
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput), throughput >= 20);
	}
	
	class HelloWorldScenario implements Scenario {
	    public void execute() {
	        System.out.println("Hello World");
	    }
	}

Want to write your scenarios using JBehave instead? No problem...

	@Test
	public void testTheSystemSupportsAtLeast2JBehaveScenariosPerSecond() {
	
	    ConcurrentTestRunner runner = new ConcurrentTestRunner(new JBehaveScenario("foo.txt"), MAX_THROUGHPUT, DURATION);
	    runner.run();
	    int throughput = runner.actualThroughput();
	    
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput), throughput >= 2);
	}

You can also run different scenarios in parallel using the MultiConcurrentTestRunner... 

	@Test
	public void testTheSystemSupportsRunningDifferentScenariosInParallel() {
	
	    ConcurrentTestRunner runner1 = new ConcurrentTestRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
	    ConcurrentTestRunner runner2 = new ConcurrentTestRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, DURATION);
	
	    MultiConcurrentTestRunner multiTestRunner = new MultiConcurrentTestRunner(runner1, runner2);
	    multiTestRunner.run();
	
	    int throughput1 = runner1.actualThroughput();
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput1), throughput1 >= 20);
	    
	    int throughput2 = runner2.actualThroughput();
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput2), throughput2 >= 20);
	}

See the unit tests for more examples.
