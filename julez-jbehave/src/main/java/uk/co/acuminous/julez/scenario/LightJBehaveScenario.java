package uk.co.acuminous.julez.scenario;

import java.net.URL;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.StoryRunner;
import org.jbehave.core.embedder.StoryRunner.State;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.StepCollector.Stage;

public class LightJBehaveScenario extends BaseScenario {

    private final URL codeLocation;
    private final String scenario;
    private final Object[] steps;

    public LightJBehaveScenario(URL codeLocation, String scenario, Object... steps) {
        this.codeLocation = codeLocation;
        this.scenario = scenario;
        this.steps = steps;
    }    
    
    @Override
    public void run() {

    	onEvent(eventFactory.begin());            
        
        try {
            Configuration configuration = new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep());
            InstanceStepsFactory stepsFactory = new InstanceStepsFactory(configuration, steps);
            List<CandidateSteps> candidateSteps = stepsFactory.createCandidateSteps();
            StoryRunner storyRunner = new StoryRunner();
            Story story = storyRunner.storyOfPath(configuration, getStoryPath());
            
            State beforeStories = storyRunner.runBeforeOrAfterStories(configuration, candidateSteps, Stage.BEFORE);
            if ( storyRunner.failed(beforeStories) ){
                throw new RuntimeException("Error before stories");
            }
            
            storyRunner.run(configuration, stepsFactory, story, new MetaFilter("", null), beforeStories);
            
            State afterStories = storyRunner.runBeforeOrAfterStories(configuration, candidateSteps, Stage.AFTER);
            if ( storyRunner.failed(afterStories) ){
                throw new RuntimeException("Error after stories");
            }            
            onEvent(eventFactory.pass());            
        } catch (Throwable e) {
        	onEvent(eventFactory.error());            
        }
        
    }
    
    private String getStoryPath() {
        List<String> storyPaths = new StoryFinder().findPaths(codeLocation, scenario, "");
        if (storyPaths.isEmpty()) {
            throw new RuntimeException(String.format("Cannot find story for %s", scenario));
        }
        return storyPaths.get(0);
    }    

}
