package uk.co.acuminous.julez.test;

import java.sql.SQLException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class TestUtils {

    private static ThreadLocal<BrokerService> brokerHolder = new ThreadLocal<BrokerService>();
    
    public static SingleConnectionDataSource getDataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:julez");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    public static void nukeDatabase() {
        try {
            getDataSource().getConnection().prepareStatement("shutdown").execute();
        } catch (SQLException e) {
            // Meh
        }
    }

    public static BrokerService createBroker() {
        
        try {        
            BrokerService broker = new BrokerService();
            broker.setPersistent(false);
            broker.start();
            brokerHolder.set(broker);
            return broker;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void nukeBroker() {
        try {
            brokerHolder.get().stop();
            brokerHolder.remove();
        } catch (Exception e) {
            // Meh
        }
    }

    public static ActiveMQConnectionFactory getConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

}
