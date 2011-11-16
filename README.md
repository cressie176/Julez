Julez
====================
Julez is an extremely lightweight toolkit for running simple performance tests via jUnit.
You write a "Scenario" using your test tool of choice (junit, htmlunit, jbehave, selenium, etc), 
then use Julez to run the scenario repeatedly from multiple threads. e.g.

    @Test
    public void demonstrateASimplePerformanceTest() {

        HelloWorldScenario scenario = new HelloWorldScenario();

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerListeners(throughputMonitor);                        

        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);        
        
        new ConcurrentScenarioRunner().queue(scenarios).run();

        assertMinimumThroughput(2000, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        public void run() {
            start();
            System.out.print("Hello World ");
            pass();
        }
    }

Want to write your scenarios using JBehave instead? Here's how...

    @Test
    public void demonstrateASimpleJBehavePerformanceTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerListeners(throughputMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);
        
        new ConcurrentScenarioRunner().queue(scenarios).timeOutAfter(30, SECONDS).run();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
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
	        
	        try {
	            start();            
	            embedder.runStoriesAsPaths(storyPaths);
	            pass();
	        } catch (Throwable t) {
	            fail();
	        }        
	    }
	}	

You can also run different scenarios in parallel using the MultiConcurrentScenarioRunner... 

    @Test
    public void demonstrateMultipleScenariosInParellel() {

        ThroughputMonitor combinedMonitor = new ThroughputMonitor();        
        ThroughputMonitor monitor1 = new ThroughputMonitor();
        ThroughputMonitor monitor2 = new ThroughputMonitor();
        
        ScenarioRunner runner1 = prepareForTestRun(new HelloWorldScenario(), 100, combinedMonitor, monitor1);
        ScenarioRunner runner2 = prepareForTestRun(new GoodbyeWorldScenario(), 50, combinedMonitor, monitor2);        

        new MultiConcurrentScenarioRunner(runner1, runner2).run();

        assertMinimumThroughput(500, monitor1.getThroughput());
        assertMinimumThroughput(250, monitor2.getThroughput());
        assertMinimumThroughput(750, combinedMonitor.getThroughput());
    }

    private ScenarioRunner prepareForTestRun(Scenario scenario, int size, ScenarioEventHandler... listeners) {
        scenario.registerListeners(listeners);        
        Scenarios scenarios = TestUtils.getScenarios(scenario, size);
        return new ConcurrentScenarioRunner().queue(scenarios);
    }
	
You can record results asynchronously to a database for trending / reports	
    
    @Test    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {        
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps());        

        ScenarioEventJdbcRepository repository = new ScenarioEventJdbcRepository(dataSource).ddl();
        ScenarioEventJmsListener jmsListener = new ScenarioEventJmsListener(connectionFactory);
        jmsListener.registerListeners(repository);
        jmsListener.listen();
        
        ScenarioEventJmsSender jmsSender = new ScenarioEventJmsSender(connectionFactory);               
        scenario.registerListeners(jmsSender);
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);  
        
        new ConcurrentScenarioRunner().queue(scenarios).run();
        
        jmsListener.shutdownGracefully();
        
        assertEquals(200, repository.count());                        
    }

Best of all, because they're just junit tests you can schedule them from your CI environment.

See the "example" tests for more detail.

Project Status
---------------------
The project is in very early stages of development. There's guaranteed to be a lot of API thrashing.

Road Map (In no particular order)
---------------------
* Throttled ScenarioQueue
* ScenarioQueue that can be asynchronously filled (maybe when the size reaches a threshold)
* Mechanism to assign junit tests to profiles so that can be run / skipped
* Create a proper event model for scenarios and scenario runner
* Mechanism to configure a synchronised start from command line so the suites can be synchronised from multiple hosts
* Create a web based admin ui for monitoring status of a test run in realtime
* Separate out Examples, JBehave, JMS & JDBC classes into different projects so the core is ultra lightweight