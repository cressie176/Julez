package uk.co.acuminous.julez.recorder;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.result.Result;
import uk.co.acuminous.julez.result.ResultStatus;
import uk.co.acuminous.julez.test.TestUtils;
import uk.co.acuminous.julez.util.JmsHelper;

import com.google.gson.Gson;

public class JmsResultRecorderTest {

    private QueueConnectionFactory connectionFactory;
    private ResultRecorder resultRecorder;

    @Before
    public void init() throws Exception {
                
        TestUtils.createBroker();
        connectionFactory = TestUtils.getConnectionFactory();        

        resultRecorder = new JmsResultRecorder(connectionFactory, new TestResultFactory());
    }
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker();
    }
    
    @Test
    public void failuresAreWrittenToTheResultsQueue() throws JMSException, InterruptedException {        
        resultRecorder.fail("foo");        
        Result result = new Gson().fromJson(getOnlyMessage().getText(), Result.class);        
        assertResult(result, ResultStatus.FAIL, "foo");
    } 
        
    @Test
    public void passesAreWrittenToTheResultsQueue() throws Exception {        
        resultRecorder.pass("bar");        
        Result result = new Gson().fromJson(getOnlyMessage().getText(), Result.class);        
        assertResult(result, ResultStatus.PASS, "bar");        
    }
        
    @Test
    public void passesWithoutDescriptionsAreWrittenToTheResultsQueue() throws Exception {
        resultRecorder.pass();        
        Result result = new Gson().fromJson(getOnlyMessage().getText(), Result.class);        
        assertResult(result, ResultStatus.PASS, "");                        
    }
        
    @Test
    public void countsNumberOfFailures() throws Exception {
        for (int i = 0; i < 10; i++) {
            resultRecorder.fail(String.valueOf(i));
        }
        
        assertEquals(10, resultRecorder.failureCount());
    }    
    
    @Test
    public void countsNumberOfPasses() throws Exception {
        for (int i = 0; i < 10; i++) {
            resultRecorder.pass(String.valueOf(i));
        }
        
        assertEquals(10, resultRecorder.passCount());
        
        for (int i = 0; i < 5; i++) {
            resultRecorder.pass(String.valueOf(i));
        }

        assertEquals(15, resultRecorder.passCount());        
    }    
    
    @Test
    public void calulcatesThePercentage() {
        for (int i = 0; i < 10; i++) {
            resultRecorder.pass();
        }
        
        for (int i = 0; i < 5; i++) {
            resultRecorder.fail("");
        }        
        
        assertEquals(66, resultRecorder.percentage());        
    }
    
    public void recorderBlocksUntilComplete() throws Exception {
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(new Runnable() {
            @Override public void run() {
                resultRecorder.shutdownGracefully();  
                latch.countDown();
            }            
        }).start();
        
        latch.await(5, SECONDS);
        
        assertEquals(1, latch.getCount());
    }
    
    @Test(timeout=2000)
    public void recorderAbortsCompleteAfterTimeout() throws Exception {
        resultRecorder.pass();
        resultRecorder.complete(1);
    }    

    private TextMessage getOnlyMessage() {
        List<TextMessage> messages = getMessages();
        
        assertEquals(1, messages.size());
        return messages.get(0);
    }

    private List<TextMessage> getMessages() {
        return JmsHelper.browseMessages(connectionFactory, JmsResultRecorder.DEFAULT_QUEUE_NAME);        
    }
      
    private void assertResult(Result result, ResultStatus status, String description) {
        assertEquals("Julez", result.getSource());
        assertEquals("ABC", result.getRun());
        assertEquals(1234, result.getTimestamp());
        assertEquals("XYZ", result.getScenarioName());
        assertEquals(status, result.getStatus());        
        assertEquals(description, result.getDescription());
    }      
    
    class TestResultFactory extends DefaultResultFactory {
        
        public TestResultFactory() {
            super(null);
        }

        @Override public Result getInstance(ResultStatus status, String description) {
            return new Result("Julez", "ABC", "XYZ", 1234, status, description);
        }
    }
}
