# Julez

An extremely lightweight toolkit for running simple performance tests via jUnit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave, selenium, etc), 
then use Julez to run the scenario repeatedly from multiple threads at a capped rate. e.g.

    @Test(timeout=TEST_TIMEOUT)
	public void demonstrateASimplePerformanceTest() {
	
		Scenario scenario = new HelloWorldScenario();
	    ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, DURATION);
	    runner.run();
	    
	    assertMinimumThroughput(20, runner.actualThroughput());
	}
	
	class HelloWorldScenario implements Scenario {
	    public void execute() {
	        System.out.println("Hello World");
	    }
	}

Want to write your scenarios using JBehave instead? Here's how...

    @Test(timeout=TEST_TIMEOUT)
	public void testJBehaveScenario() {
	
        JBehaveScenario scenario = new JBehaveScenario("scenario1.txt", new Scenario1Steps());
	    ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, DURATION);
	    runner.run();
	    
	    assertMinimumThroughput(2, runner.actualThroughput());
	}
	
	public class JBehaveScenario implements Scenario {
	
	    private final URL codeLocation;    
	    private final String scenario;
	    private final Object[] steps;
	
	    public JBehaveScenario(URL codeLocation, String scenario, Object... steps) {
	        this.codeLocation = codeLocation;
	        this.scenario = scenario;
	        this.steps = steps;
	    }
	
	    public void execute() {
	        Embedder embedder = new Embedder();
	        embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
	        embedder.embedderControls().doIgnoreFailureInStories(true);
	        embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
	        embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), steps).createCandidateSteps());
	
	        List<String> storyPaths = new StoryFinder().findPaths(codeLocation, scenario, "");
	        if (storyPaths.isEmpty()) {
	            throw new RuntimeException(String.format("Cannot find story for %s", scenario));
	        }
	        
	        embedder.runStoriesAsPaths(storyPaths);
	    }
	}	

You can also run different scenarios in parallel using the MultiConcurrentScenarioRunner... 

    @Test(timeout=TEST_TIMEOUT)
    public void demonstrateMultipleScenariosInParellel() throws Throwable {
	
	    ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
	    ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, DURATION);
	
	    MultiConcurrentScenarioRunner multiTestRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
	    multiTestRunner.run();
	
	    assertMinimumThroughput(20, runner1.actualThroughput());
	    assertMinimumThroughput(20, runner2.actualThroughput());	
	}
	
You can record results asynchronously to a database for trending / reports	

    @Test(timeout=TEST_TIMEOUT)    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {
        
        JmsResultRecorder recorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        JBehaveScenario scenario = new JBehaveScenario(codeLocationFromClass(this.getClass()), "scenario2.txt", new Scenario2Steps(recorder));
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);        
        runner.run();
        
        recorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        resultRepository.dump(ResultStatus.FAIL);
                
        assertMinimumThroughput(5, runner.actualThroughput());
        assertPassMark(95, recorder.percentage()); 
    }

Best of all, because they're just junit tests you can schedule them from your CI environment.

See the "example" tests for more detail.
