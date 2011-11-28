package uk.co.acuminous.julez.scenario;

import java.net.URL;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.CandidateSteps;
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

        try {
            Embedder embedder = getEmbedder();
            EventRaisingEmbedderMonitor monitor = new EventRaisingEmbedderMonitor(eventFactory);
            embedder.useEmbedderMonitor(monitor);

            raise(eventFactory.begin());

            embedder.runStoriesAsPaths(getStoryPaths());
            
            if (monitor.receivedEvent()) {
                raise(monitor.getEvent());
            } else {
                raise(eventFactory.pass());
            }
        } catch (Throwable t) {
            ScenarioEvent event = eventFactory.error();
            event.getData().put("message", t.getMessage());
            raise(event);
        } finally {
            raise(eventFactory.end());
        }
    }

    private Embedder getEmbedder() {
        Embedder embedder = new Embedder();

        embedder.embedderControls().doIgnoreFailureInStories(true);

        Configuration embedderConfiguration = new MostUsefulConfiguration().usePendingStepStrategy(new FailingUponPendingStep());
        embedder.useConfiguration(embedderConfiguration);

        List<CandidateSteps> candidateSteps = new InstanceStepsFactory(embedderConfiguration, steps).createCandidateSteps();
        embedder.useCandidateSteps(candidateSteps);

        return embedder;
    }

    private List<String> getStoryPaths() {
        List<String> storyPaths = new StoryFinder().findPaths(codeLocation, scenario, "");
        if (storyPaths.isEmpty()) {
            throw new RuntimeException(String.format("Cannot find story for %s", scenario));
        }
        return storyPaths;
    }
}
