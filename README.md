# Julez
====================

An extremely lightweight toolkit for running simple performance tests via jUnit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave, selenium, etc), 
then use Julez to run the scenario repeatedly from multiple threads. e.g.

    @Test
    public void demonstrateASimplePerformanceTest() {

        Scenarios scenarios = TestUtils.getScenarios(new HelloWorldScenario(), 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.run();

        assertMinimumThroughput(10000, runner.throughput());
    }

    class HelloWorldScenario extends BaseScenario {
        public void run() {
            System.out.print("Hello World ");
            notifyComplete();
        }
    }

Want to write your scenarios using JBehave instead? Here's how...

    @Test
    public void demonstrateASimpleJBehavePerformanceTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios).timeOutAfter(30, SECONDS);
        runner.run();

        assertMinimumThroughput(5, runner.throughput());
    }
	
	public class JBehaveScenario extends BaseScenario {
	
	    private final URL codeLocation;    
	    private final String scenario;
	    private final Object[] steps;
	
	    public JBehaveScenario(URL codeLocation, String scenario, Object... steps) {
	        this.codeLocation = codeLocation;
	        this.scenario = scenario;
	        this.steps = steps;
	    }
	
	    public void run() {
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
	        
	        notifyComplete();
	    }
	}	

You can also run different scenarios in parallel using the MultiConcurrentScenarioRunner... 

    public void demonstrateMultipleScenariosInParellel() {

        Scenarios helloWorldScenarios = TestUtils.getScenarios(new HelloWorldScenario(), 100);
        ScenarioRunner runner1 = new ConcurrentScenarioRunner().queue(helloWorldScenarios);

        Scenarios goodbyeWorldScenarios = TestUtils.getScenarios(new GoodbyeWorldScenario(), 100);
        ScenarioRunner runner2 = new ConcurrentScenarioRunner().queue(goodbyeWorldScenarios);

        ScenarioRunner multiRunner = new MultiConcurrentScenarioRunner(runner1, runner2);
        multiRunner.run();

        assertMinimumThroughput(1000, runner1.throughput());
        assertMinimumThroughput(1000, runner2.throughput());
        assertMinimumThroughput(2000, multiRunner.throughput());
    }
	
You can record results asynchronously to a database for trending / reports	
    
    @Test    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {
        
        JmsResultRecorder resultRecorder = new JmsResultRecorder(connectionFactory, new DefaultResultFactory("Scenario 2"));
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps(resultRecorder));        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);               
        
        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios); 
        runner.run();
        
        resultRecorder.shutdownGracefully();        
        resultListener.shutdownGracefully();
        
        resultRepository.dump(ResultStatus.FAIL);
                        
        assertMinimumThroughput(5, runner.throughput());
        assertPassMark(95, resultRecorder.percentage()); 
    }

Best of all, because they're just junit tests you can schedule them from your CI environment.

See the "example" tests for more detail.

# Project Status
====================
The project is in very early stages of development. There's guaranteed to be a lot of API thrashing.

# Road Map (In no particular order)
====================
* Throttled ScenarioQueue
* ScenarioQueue that can be asynchronously filled (maybe when the size reaches a threshold)
* Mechanism to assign junit tests to profiles so that can be run / skipped
* Create a proper event model for scenarios and scenario runner
* Move throughput calculation from ConcurrentScenarioRecorder to the results recorder
* Mechanism to configure a synchronised start from command line so the suites can be synchronised from multiple hosts
* Make the JdbcResultRepository and JmsResultListener implement ResultRecorder
* Make ResultRecorders chainable
* Create a web based admin ui for monitoring status of a test run in realtime
* Separate out JMS & JDBC classes into a different project so the core is ultra lightweight