package examples.jbehave;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import uk.co.acuminous.julez.event.handler.ResultMonitor;
import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.event.pipe.FanOutPipe;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.JBehaveStoryRunnerScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.util.PerformanceAssert;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

public class InceptionSteps {

    private ThroughputMonitor throughputMonitor;
    private ResultMonitor resultMonitor;
    private FanOutPipe monitors = new FanOutPipe();
    
    @Given("a throughput monitor")
    public void aThroughputMonitor() {
        throughputMonitor = new ThroughputMonitor();
        monitors.register(throughputMonitor);
    }
    
    @Given("a result monitor")
    public void aResultMonitor() {
        resultMonitor = new ResultMonitor();
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
        
        new ConcurrentScenarioRunner().register(monitors).allocate(numThreads, THREADS).queue(new ScenarioHopper(list)).go();
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
