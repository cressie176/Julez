package uk.co.acuminous.julez.recorder;

import static org.junit.Assert.assertEquals;

import javax.jms.QueueConnectionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.acuminous.julez.result.InMemoryResultRepository;
import uk.co.acuminous.julez.result.JmsResultListener;
import uk.co.acuminous.julez.result.Result;
import uk.co.acuminous.julez.result.ResultRepository;
import uk.co.acuminous.julez.result.ResultStatus;
import uk.co.acuminous.julez.test.TestUtils;

public class JmsResultListenerTest {

    private QueueConnectionFactory connectionFactory;
    private ResultRepository resultRepository;    
    private JmsResultListener listener;

    @Before
    public void init() throws Exception {
        TestUtils.createBroker();
        
        connectionFactory = TestUtils.getConnectionFactory();        
                        
        resultRepository = new InMemoryResultRepository();
        listener = new JmsResultListener(connectionFactory, resultRepository).listen();                        
    }
    
    @After
    public void nuke() throws Exception {
        TestUtils.nukeBroker(); 
    }
    
    @Test
    public void resultsAreAddedToTheRepository() throws InterruptedException {
        putAPassOnTheQueue();
                
        listener.shutdownWhenQuiet(5);
        
        assertEquals(1, resultRepository.count());
    }

    private void putAPassOnTheQueue() {
        new JmsResultRecorder(connectionFactory, new TestResultFactory()).pass();
    }
    
    class TestResultFactory extends DefaultResultFactory {
        
        public TestResultFactory() {
            super(null);
        }

        @Override public Result getInstance(ResultStatus status, String description) {
            return new Result("id", "source", "run", "scenario", 1234, ResultStatus.FAIL, "foo");
        }
    }    
}
