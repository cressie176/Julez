package uk.co.acuminous.julez.recorder;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import uk.co.acuminous.julez.result.Result;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.result.ResultStatus;
import uk.co.acuminous.julez.util.JmsHelper;

public class JmsResultRecorder extends BaseResultRecorder {

    public static final String DEFAULT_QUEUE_NAME = "julez";
    private final QueueConnectionFactory connectionFactory;    
    private final QueueConnection connection;
    private final String queueName;
    private final ResultFactory resultFactory;    
    private final AtomicInteger failures = new AtomicInteger();
    private final AtomicInteger passes = new AtomicInteger();
    
    public JmsResultRecorder(QueueConnectionFactory connectionFactory, ResultFactory resultFactory) {  
        this(connectionFactory, DEFAULT_QUEUE_NAME, resultFactory);
    }
        
    public JmsResultRecorder(QueueConnectionFactory connectionFactory, String queueName, ResultFactory resultFactory) {        
        this.connectionFactory = connectionFactory;
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;        
        this.resultFactory = resultFactory;
    }
    
    @Override
    public int passCount() {
        return passes.get();
    }

    @Override
    public int failureCount() {
        return failures.get();
    }

    @Override
    public void pass(String description) {
        passes.incrementAndGet();
        Result result = resultFactory.getInstance(ResultStatus.PASS, description);
        JmsHelper.send(connection, queueName, result.toJson());
    }

    @Override
    public void fail(String description) {
        failures.incrementAndGet();
        Result result = resultFactory.getInstance(ResultStatus.FAIL, description);
        JmsHelper.send(connection, queueName, result.toJson());
    }

    @Override
    public void shutdownGracefully() {
        try {            
            while (!JmsHelper.browseMessages(connectionFactory, queueName).isEmpty()) {
                Thread.sleep(1000);            
            }
        } catch (InterruptedException e) {
            // Meh
        } finally {            
            shutdown();
        }
    }
    
    public void shutdown() {
        JmsHelper.close(connection);        
    }
    
    @Override
    protected void finalize() {
        JmsHelper.close(connection);
    }  
}
