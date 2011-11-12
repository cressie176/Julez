# Julez

An extremely lightweight framework for running simple performance tests via junit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave etc), 
then use Julez to run the scenario repeatedly from multiple threads at a capped rate. e.g.

	@Test
	public void testSimpleScenario() {
	
	    ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
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
	
	    ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(new JBehaveScenario("foo.txt"), MAX_THROUGHPUT, DURATION);
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

You can also run different scenarios in parallel using the MultiConcurrentScenarioRunner... 

	@Test
	public void testParallelScenarios() {
	
	    ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
	    ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, DURATION);
	
	    MultiConcurrentScenarioRunner multiTestRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
	    multiTestRunner.run();
	
	    int throughput1 = runner1.actualThroughput();
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput1), throughput1 >= 20);
	    
	    int throughput2 = runner2.actualThroughput();
	    assertTrue(String.format("Actual throughput: %d scenarios per second", throughput2), throughput2 >= 20);
	}

See the unit tests for more examples.
