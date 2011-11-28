Julez Core
====================
Julez core is an lightweight and extensible toolkit for concurrency testing in java. 
You can use it to help ensure that your code is thread safe, to detect deadlocks, 
to soak test and load test. Because Julez is just a Java library, and not a standalone 
test tool you can run it as you would a normal junit test, from within your IDE and 
from your continuous integration environment. If you have a server farm you could even 
configure your build to run load tests from several client machines at once.

At the heart of Julez are scenarios. You write scenarios using your test tools of choice 
(junit, htmlunit, jbehave, selenium, etc), then use Julez to run the scenario repeatedly 
from multiple threads. If you want to capture statistics while your scenarios are 
running they you can through events, then configure event handlers to respond to those 
events accordingly. e.g.

    @Test
    public void demonstrateAConcurrentThroughputTest() {

        HelloWorldScenario scenario = new HelloWorldScenario();

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);                        

        ScenarioSource scenarios = ScenarioRepeater.getScenarios(scenario, 100);        
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.registerEventHandler(throughputMonitor);
        runner.queue(scenarios).run();

        assertMinimumThroughput(500, throughputMonitor.getThroughput());
    }

    class HelloWorldScenario extends BaseScenario {        
        
        public void run() {
            raise(eventFactory.begin());
            System.out.print("Hello World ");
            raise(eventFactory.end());
        }
    }

Julez includes optional support for JBehave (for more expressive scenarios), JMS (for 
asynchronous / remote event handling) and JDBC (for post run analysis and trending).

See "Julez Examples" for more details

Project Status
---------------------
The project is in very early stages of development. There's guaranteed to be a lot of API thrashing.

Road Map (In no particular order)
---------------------
* ScenarioQueue that can be asynchronously filled (maybe when the size reaches a threshold)
* Mechanism to assign junit tests to profiles so that can be run / skipped
* Mechanism to configure a synchronised start from command line so the suites can be synchronised from multiple hosts
* Create a web based admin ui for monitoring status of a test run in realtime