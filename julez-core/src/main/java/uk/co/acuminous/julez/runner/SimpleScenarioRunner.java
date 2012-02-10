package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.executor.SequentialScenarioExecutor;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;

public class SimpleScenarioRunner extends BaseScenarioRunner {

    private ScenarioSource scenarios = new ScenarioHopper();
    private ScenarioExecutor executor = new SequentialScenarioExecutor();
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();
    private boolean stopping = false;

    @Override
    public void start() {
        handler.onEvent(eventFactory.begin()); 
        
        Scenario scenario = getNextScenario();
        while (scenario != null) {
            executor.execute(scenario);
            scenario = getNextScenario();
        }
        
        handler.onEvent(eventFactory.end());
    }
    
    private Scenario getNextScenario() {
        return !stopping ? scenarios.next() :  null;
    }
    
    @Override    
    public void stop() {
        stopping = true;
    }
    

    public SimpleScenarioRunner queue(ScenarioSource scenarios) {
        this.scenarios = scenarios;
        return this;        
    }

    public SimpleScenarioRunner assign(ScenarioExecutor executor) {
        this.executor = executor;
        return this;
    }
    
    public SimpleScenarioRunner register(EventHandler handler) {
        super.register(handler);
        return this;
    }    

}
