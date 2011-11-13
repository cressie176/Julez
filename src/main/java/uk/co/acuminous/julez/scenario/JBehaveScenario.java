package uk.co.acuminous.julez.scenario;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

import java.util.List;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.Embedder.RunningStoriesFailed;
import org.jbehave.core.embedder.SilentEmbedderMonitor;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InstanceStepsFactory;


public class JBehaveScenario implements Scenario {

    private final String scenario;
    private final Object[] steps;

    public JBehaveScenario(String scenario, Object... steps) {
        this.scenario = scenario;
        this.steps = steps;
    }

    public void execute() {
        Embedder embedder = new Embedder();
        embedder.useEmbedderMonitor(new SilentEmbedderMonitor(null));
        embedder.useConfiguration(new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep()));
        embedder.useCandidateSteps(new InstanceStepsFactory(embedder.configuration(), steps).createCandidateSteps());

        List<String> storyPaths = new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), scenario, "");
        
        try {
            embedder.runStoriesAsPaths(storyPaths);
        } catch (RunningStoriesFailed e) {
            // Test probably finished leaving some stories queued
        }
    }
}
