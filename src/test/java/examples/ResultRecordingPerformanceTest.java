package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertPassMark;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.InMemoryResultRecorder;
import uk.co.acuminous.julez.recorder.ResultRecorder;
import uk.co.acuminous.julez.result.DefaultResultFactory;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.Scenarios;
import uk.co.acuminous.julez.scenario.ThroughputMonitor;
import uk.co.acuminous.julez.test.TestUtils;

public class ResultRecordingPerformanceTest {

    @Test
    public void demonstrateRecordingScenarioResults() {
        
        ResultFactory resultFactory = new DefaultResultFactory("Some Scenario");
        ResultRecorder resultRecorder = new InMemoryResultRecorder(resultFactory);

        ResultRecordingScenario scenario = new ResultRecordingScenario(resultRecorder);
        
        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.registerListeners(throughputMonitor);        
        
        Scenarios scenarios = TestUtils.getScenarios(scenario, 100);
        
        new ConcurrentScenarioRunner().queue(scenarios).run();

        resultRecorder.shutdownGracefully();

        assertMinimumThroughput(5000, throughputMonitor.getThroughput());
        assertPassMark(20, resultRecorder.percentage());
    }

    class ResultRecordingScenario extends BaseScenario {

        private final ResultRecorder recorder;

        public ResultRecordingScenario(ResultRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            start();
            if (RandomUtils.nextBoolean()) {
                recorder.fail("on noes");
            } else {
                recorder.pass();
            }
            pass();
        }
    }

}
