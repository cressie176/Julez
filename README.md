Julez
====================
Julez is an extremely lightweight toolkit for concurrency testing in java. You can use it 
to help ensure that your code is thread safe, to detect deadlocks, to soak test or load test.
Because Julez is just a Java library, and not a standalone test tool you can run it as you 
would a normal junit test, from within your IDE and from your continuous integration 
environment. If you have a server farm you could even configure your build to run load 
tests from several client machines at once.

At the heart of Julez are scenarios. You write scenarios using your test tools of choice 
(junit, htmlunit, jbehave, selenium, etc), then use Julez to run the scenario repeatedly 
from multiple threads. If you want to capture performance statistics while your scenarios are 
running they you can through events, and configure event handles to listen for them and 
respond accordingly. The following example demonstrates a simple "Hello World" scenario 
which is run 100 times concurrently by 10 threads (the default for the ConcurrentScenarioRunner)
The throughput monitor listens for scenario (pass) and scenario runner events (start, stop) 
in order to calculate the throughput.

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

It's common to write functional or acceptance tests for web applications with tools like webdriver, selenium and html unit, but less 
common to use these tool for non functional tests like deadlock detection, load testing or load testing. Part of the reason for this 
is because these tools are slower than the protocol level record and playback approach taken by many performance test tools. I accept 
this, but think that given the increasing use of virtualisation, and easy availability of powever continuation servers it's 
becoming less necessary. If your application only needs to support 50 requests / second and using html unit and jbehave you can 
generate 10 requests per second, and you have a virtual server farm of 5 servers you could easily configure your build system to 
run nightly performance tests, written in JBehave's given - when - then syntax, and executed from htmlunt or web drvier.

    @Test
    public void demonstrateASimpleWebPerformanceTest() {

        SimpleWebScenario scenario = new SimpleWebScenario();
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerListeners(throughputMonitor);                                
        
        new ConcurrentScenarioRunner().queue(scenarios).run();

        assertMinimumThroughput(14, throughputMonitor.getThroughput());
    }

    class SimpleWebScenario extends BaseScenario {

        public void run() {
            start();
            WebClient webClient = new WebClient();
            try {                
                webClient.setCssEnabled(false);
                webClient.setJavaScriptEnabled(false);                
                
                HtmlPage page = webClient.getPage("http://localhost:8080");
                if (page.getWebResponse().getStatusCode() == 200) {
                    pass();
                } else {                                               
                    fail();
                }
            } catch (Exception e) {
                fail();
            } finally {                
                webClient.closeAllWindows();
            }
        }
    }

Want to write your scenarios using JBehave? Here's how...

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
For realistic metrics you'll need to run
multiple different scenarios concurrently, at a capped throughput and in the correct ratio. Julez supports this 
via the MutiConcurentScenarioRunner. e.g.

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
	
Once you start building up a set of healthy performance tests you'll probably want to analyse the statistics.
Julez provides some analysers, such as the ThroughputMonitor and ResultMonitor, but it's always been expected 
that you'll want to write your own. We also provide a mechanism for persisting events asynchronously to a 
database so that you can analyse, report and trend the data using a whole host of 3rd party products. 
	    
    @Test    
    public void demonstrateRecordingScenarioResultsAsynchronouslyToADatabase() {        
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario2.txt", new Scenario2Steps());        

        ScenarioEventJdbcRepository repository = new ScenarioEventJdbcRepository(dataSource);
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