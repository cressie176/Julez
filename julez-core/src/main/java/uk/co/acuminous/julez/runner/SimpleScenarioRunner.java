package uk.co.acuminous.julez.runner;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.executor.SynchronousScenarioExecutor;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;

public class SimpleScenarioRunner extends BaseScenarioRunner {

    private ScenarioSource scenarios = new ScenarioHopper();
    private ScenarioExecutor executor = new SynchronousScenarioExecutor();
    private ScenarioRunnerEventFactory eventFactory = new ScenarioRunnerEventFactory();
    private boolean stopping = false;

    @Override
    public void start() {
        executeScenarios();        
        shutdownWhenDone();        
    }

    private void executeScenarios() {
        handler.onEvent(eventFactory.begin());        
        Scenario scenario = getNextScenario();
        while (scenario != null) {
            executor.execute(scenario);
            scenario = getNextScenario();
        }
    }    
        
    private Scenario getNextScenario() {
        return !stopping ? scenarios.next() :  null;
    }    
    
    private void shutdownWhenDone() {
        try {
            executor.awaitTermination();
        } catch (InterruptedException e) {
            // Meh
        } finally {
            stop();    
        }
    }
    
    @Override    
    public synchronized void stop() {
        if (!stopping) {
            try {
                stopping = true;
                executor.shutdown();
            } finally {
                handler.onEvent(eventFactory.end());                
            }
        }
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
    
    public SimpleScenarioRunner useEventFactory(ScenarioRunnerEventFactory eventFactory) {
        this.eventFactory = eventFactory;
        return this;
    }    

}
