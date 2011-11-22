package test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class JmsTestUtils {

    private static ThreadLocal<BrokerService> brokerHolder = new ThreadLocal<BrokerService>();

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
            brokerHolder.get().waitUntilStopped();
            brokerHolder.remove();
        } catch (Exception e) {
            // Meh
        }
    }

    public static ActiveMQConnectionFactory getConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }
}
