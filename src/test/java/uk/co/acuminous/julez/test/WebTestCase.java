package uk.co.acuminous.julez.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;


public class WebTestCase {

    private static Server server;    
    
    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(8080);               

        Context root = new Context(server, "/", Context.SESSIONS);
        root.addServlet(new ServletHolder(new DemoServlet()), "/");
        server.start();             
    }
    
    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }    
    
}
