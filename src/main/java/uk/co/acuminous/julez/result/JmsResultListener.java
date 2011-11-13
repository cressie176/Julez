package uk.co.acuminous.julez.result;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;

import uk.co.acuminous.julez.recorder.JmsResultRecorder;
import uk.co.acuminous.julez.util.JmsHelper;

public class JmsResultListener implements MessageListener, Runnable {

    private final QueueConnection connection;    
    private final String queueName;
    private final ResultRepository repository;
    private QueueSession session;
    private long lastReceivedTimestamp = System.currentTimeMillis();
    private Thread listenerThread;    
    
    public JmsResultListener(QueueConnectionFactory connectionFactory, ResultRepository repository) {
        this(connectionFactory, JmsResultRecorder.DEFAULT_QUEUE_NAME, repository);
    }
    
    public JmsResultListener(QueueConnectionFactory connectionFactory, String queueName, ResultRepository repository) {
        this.connection = JmsHelper.getConnection(connectionFactory);
        this.queueName = queueName;        
        this.repository = repository;               
    }

    public void run() {
        synchronized(this) {
            try {
                this.session = JmsHelper.registerListener(queueName, connection, this);
                wait();
            } catch (InterruptedException e) {
                // Meh
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    

    @Override
    public void onMessage(Message message) {
        lastReceivedTimestamp = System.currentTimeMillis();
        String json = JmsHelper.getText(message);
        Result result = Result.fromJson(json);
        repository.add(result);        
    }
    
    public void shutdownGracefully() {
        while (System.currentTimeMillis() - lastReceivedTimestamp < 10000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Meh
            }
        }
        shutdown();
    } 
    
    public void shutdown() {
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }                
        JmsHelper.close(session, connection);
    }
    
    @Override
    protected void finalize() {
        JmsHelper.close(session, connection);
    }

    public JmsResultListener listen() {
        listenerThread = new Thread(this);
        listenerThread.start();
        return this;
    }
}
