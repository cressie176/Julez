# Julez

An extremely lightweight framework for running simple performance tests via junit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave etc), 
then use Julez to run the scenario repeatedly from multiple threads at a capped rate. e.g.

	@Test
	public void testSimpleScenario() {
	
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

Want to write your scenarios using JBehave instead? Here's how...

	@Test
	public void testJBehaveScenario() {
	
	    ConcurrentTestRunner runner = new ConcurrentTestRunner(new JBehaveScenario("foo.txt"), MAX_THROUGHPUT, DURATION);
	    runner.run();
	    int throughput = runner.actualThroughput();
	    
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput), throughput >= 2);
	}
	
    class JBehaveScenario implements Scenario {

        private final String scenario;

        public JBehaveScenario(String scenario) {
            this.scenario = scenario;
        }

        public void execute() {
			Embedder embedder = new Embedder();
            embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
            embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
            embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), new JBehaveSteps()).createCandidateSteps());

            List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(JBehavePerformanceTest.class), scenario, "");
            embedder.runStoriesAsPaths(storyPaths);           
        }
    }	

You can also run different scenarios in parallel using the MultiConcurrentTestRunner... 

	@Test
	public void testParallelScenarios() {
	
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
