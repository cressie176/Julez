package uk.co.acuminous.julez.scenario.runner;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventDataFilter;
import uk.co.acuminous.julez.event.filter.EventFilter;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunnerEvent;
import uk.co.acuminous.julez.runner.ScenarioRunnerScenario;
import uk.co.acuminous.julez.scenario.Scenario;
import uk.co.acuminous.julez.scenario.source.ScenarioHopper;
import uk.co.acuminous.julez.test.TestEventRepository;


public class ScenarioRunningScenarioTest {

    @Test
    public void startsRunner() {
        TestEventRepository repository = new TestEventRepository();        

        EventFilter filter = new EventDataFilter().filterEventsWhere(Event.TYPE).matches(ScenarioRunnerEvent.BEGIN).register(repository);
        
        ScenarioRunner nestedRunner = new ConcurrentScenarioRunner().queue(new ScenarioHopper()).register(filter);
        Scenario scenario = new ScenarioRunnerScenario(nestedRunner);
                
        scenario.run();
        
        assertEquals(1, repository.count());
    }
}
