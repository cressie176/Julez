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
	    
	    assertThroughput(2, runner.actualThroughput());
	}
	
	public class JBehaveScenario implements Scenario {
	
	    private final String scenario;
	    private final Object[] steps;
	
	    public JBehaveScenario(String scenario, Object... steps) {
	        this.scenario = scenario;
	        this.steps = steps;
	    }
	
	    public void execute() {
	        Embedder embedder = new Embedder();
	        embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
	        embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
	        embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), steps).createCandidateSteps());
	
	        List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), scenario, "");
	        
	        try {
	            embedder.runStoriesAsPaths(storyPaths);
	        } catch (RunningStoriesFailed e) {
	            // Test probably finished leaving some stories queued
	        }
	    }
	}	

You can also run different scenarios in parallel using the MultiConcurrentScenarioRunner... 

    @Test(timeout=TEST_TIMEOUT)
    public void demonstrateMultipleScenariosInParellel() throws Throwable {
	
	    ConcurrentScenarioRunner runner1 = new ConcurrentScenarioRunner(new HelloWorldScenario(), MAX_THROUGHPUT, DURATION);
	    ConcurrentScenarioRunner runner2 = new ConcurrentScenarioRunner(new GoodbyeWorldScenario(), MAX_THROUGHPUT, DURATION);
	
	    MultiConcurrentScenarioRunner multiTestRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
	    multiTestRunner.run();
	
	    assertThroughput(20, runner1.actualThroughput());
	    assertThroughput(20, runner2.actualThroughput());	
	}
	
You can record results asynchronously to a database for trending / reports	

    @Test(timeout=TEST_TIMEOUT)    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {

        JmsResultListener resultListener = new JmsResultListener(connectionFactory, resultRepository).listen();                
        
        JmsResultRecorder resultRecorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        JBehaveScenario scenario = new JBehaveScenario("scenario2.txt", resultRecorder);
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner(scenario, MAX_THROUGHPUT, TEST_DURATION);
        
        runner.run();
        resultRecorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        assertMinimumThroughput(10, runner.actualThroughput());
        assertPassMark(90, recorder.percentage()); 
    } 

Best of all, because they're just junit tests you can schedule them from your CI environment.

See the "example" tests for more detail.
