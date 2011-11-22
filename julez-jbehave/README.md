Julez JBehave
====================
Julez JBehave adds support for JBehave to Julez, e.g.

    @Test
    public void demonstrateAConcurrentJBehaveTest() {

        URL scenarioLocation = codeLocationFromClass(this.getClass());
        JBehaveScenario scenario = new JBehaveScenario(scenarioLocation, "scenario1.txt", new Scenario1Steps());
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerEventHandler(throughputMonitor);        
        
        ScenarioSource scenarios = ScenarioRepeater.getScenarios(scenario, 100);
        
        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios).timeOutAfter(30, SECONDS);
        runner.registerEventHandler(throughputMonitor);
        runner.run();

        assertMinimumThroughput(5, throughputMonitor.getThroughput());
    }
    
See the Julez Examples project for more details    