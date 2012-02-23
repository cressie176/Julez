package examples.jbehave;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import uk.co.acuminous.julez.event.handler.ScenarioResultMonitor;
import uk.co.acuminous.julez.event.handler.ScenarioThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutEventPipe;
import uk.co.acuminous.julez.executor.ConcurrentScenarioExecutor;
import uk.co.acuminous.julez.executor.ScenarioExecutor;
import uk.co.acuminous.julez.runner.SimpleScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.util.PerformanceAssert;
import static uk.co.acuminous.julez.util.JulezSugar.*;


public class InceptionSteps {

    private ScenarioThroughputMonitor throughputMonitor;
    private ScenarioResultMonitor resultMonitor;
    private FanOutEventPipe monitors = new FanOutEventPipe();
    
    @Given("a throughput monitor")
    public void aThroughputMonitor() {
        throughputMonitor = new ScenarioThroughputMonitor();
        monitors.register(throughputMonitor);
    }
    
    @Given("a result monitor")
    public void aResultMonitor() {
        resultMonitor = new ScenarioResultMonitor();
        monitors.register(resultMonitor);
    }
    
    @When("I run $numStories $story scenarios from $n threads")
    public void run(int numStories, String story, int numThreads) {
        
        URL scenarioLocation = codeLocationFromClass(this.getClass());
        
        List<Scenario> list = new ArrayList<Scenario>();
        
        String storyPath = String.format("jbehave/" + story + ".txt");
        
        for (int i = 0; i < numStories; i++) {
            JBehaveStoryRunnerScenario scenario = new JBehaveStoryRunnerScenario(scenarioLocation, storyPath, new CalculatorSteps());
            scenario.register(monitors);
            list.add(scenario);
        }        
        
        ScenarioExecutor executor = new ConcurrentScenarioExecutor().allocate(4, THREADS);
        
        new SimpleScenarioRunner().assign(executor).register(monitors).queue(new ScenarioHopper(list)).start();
    }
    
    @Then("the minimum throughput should be $minimumThroughput $scenarios per second")
    public void verifyThroughput(int minimumThroughput) {
        PerformanceAssert.assertMinimumThroughput(minimumThroughput, throughputMonitor.getThroughput());
    }
    
    @Then("$percent% of scenarios should be successful")
    public void verifyPassMark(int percent) {
        PerformanceAssert.assertMinimumPasses(percent, throughputMonitor.getThroughput());
    }    
}
