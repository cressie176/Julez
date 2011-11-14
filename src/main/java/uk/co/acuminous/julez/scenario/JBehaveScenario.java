package uk.co.acuminous.julez.scenario;

import java.net.URL;
import java.util.List;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.SilentEmbedderMonitor;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InstanceStepsFactory;

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
        
        embedder.runStoriesAsPaths(storyPaths);
        
        notifyComplete();
    }
}
