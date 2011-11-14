package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.DefaultResultFactory;
import uk.co.acuminous.julez.recorder.InMemoryResultRecorder;
import uk.co.acuminous.julez.recorder.ResultRecorder;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.runner.ScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.test.TestUtils;

public class ResultRecordingPerformanceTest {

    @Test
    public void demonstrateRecordingScenarioResults() {

        ResultFactory resultFactory = new DefaultResultFactory("Some Scenario");
        ResultRecorder resultRecorder = new InMemoryResultRecorder(resultFactory);

        ResultRecordingScenario scenario = new ResultRecordingScenario(resultRecorder);
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);

        ScenarioRunner runner = new ConcurrentScenarioRunner().queue(scenarios);
        runner.run();

        resultRecorder.shutdownGracefully();

        assertMinimumThroughput(5000, runner.throughput());
        assertPassMark(20, resultRecorder.percentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private final ResultRecorder recorder;

        public ResultRecordingScenario(ResultRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            if (RandomUtils.nextBoolean()) {
                recorder.fail("on noes");
            } else {
                recorder.pass();
            }
            notifyComplete();
        }
    }

}
