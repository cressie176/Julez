package uk.co.acuminous.julez.util;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JmsHelper {

    public static QueueConnection getConnection(QueueConnectionFactory connectionFactory) {
        QueueConnection connection = null;        
        try {
            connection = connectionFactory.createQueueConnection();
            connection.start();
        } catch (JMSException e) {
            new RuntimeException(e);
        }
        return connection;        
    }
    
    public static void close(Object... targets) {
        for (Object target : targets) {
            try {
                Method closeMethod = target.getClass().getMethod("close");
                closeMethod.invoke(target);
            } catch (Exception e) {
                // Meh
            }
        }
    }
    
    public static QueueSession registerListener(String queueName, QueueConnection connection, MessageListener listener) {
        QueueSession session;
        try {
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            session.createConsumer(session.createQueue(queueName)).setMessageListener(listener); 
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return session;
    }

    public static String getText(Message message) {
        try {
            return ((TextMessage) message).getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public static void send(QueueConnection connection, String queueName, String msg) {
        QueueSession session = null;        
        try {
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);             
            QueueSender sender = session.createSender(session.createQueue(queueName));                                
            sender.send(session.createTextMessage(msg));        
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            JmsHelper.close(session);
        }        
    }    

    @SuppressWarnings("unchecked")
    public static List<TextMessage> browseMessages(QueueConnectionFactory connectionFactory, String queueName) {
        QueueConnection connection = null;
        QueueSession session = null;
        try {
            connection = JmsHelper.getConnection(connectionFactory);
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);;
            Queue queue = session.createQueue(queueName);        
            QueueBrowser browser = session.createBrowser(queue);
            
            return Collections.list((Enumeration<TextMessage>) browser.getEnumeration());
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            JmsHelper.close(session, connection);
        }
    }    
    
}
