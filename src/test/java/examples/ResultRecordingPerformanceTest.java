package examples;

import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumPasses;
import static uk.co.acuminous.julez.util.PerformanceAssert.assertMinimumThroughput;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import uk.co.acuminous.julez.recorder.DefaultResultFactory;
import uk.co.acuminous.julez.recorder.InMemoryResultRecorder;
import uk.co.acuminous.julez.recorder.ResultRecorder;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.scenario.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.Scenario;

public class ResultRecordingPerformanceTest {

    private static final int MAX_THROUGHPUT = 100;
    private static final int TEST_DURATION = 15;

    @Test
    public void testResultsCanBeRecorded() {

        ResultFactory resultFactory = new DefaultResultFactory("Some Scenario");
        ResultRecorder recorder = new InMemoryResultRecorder(resultFactory);
        ConcurrentScenarioRunner concurrentTestRunner = new ConcurrentScenarioRunner(new ResultRecordingScenario(recorder), MAX_THROUGHPUT, TEST_DURATION);
        concurrentTestRunner.run();
        
        recorder.shutdownGracefully();
        
        assertMinimumThroughput(20, concurrentTestRunner.actualThroughput());
        assertMinimumPasses(50, recorder.passCount());
    }

    class ResultRecordingScenario implements Scenario {
        
        private final ResultRecorder recorder;

        public ResultRecordingScenario(ResultRecorder recorder) {
            this.recorder = recorder;
        }

        public void execute() {            
            if (RandomUtils.nextBoolean()) {
                recorder.fail("on noes");
            } else {
                recorder.pass();
            }
        }
    }    
    
}
