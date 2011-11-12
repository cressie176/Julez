package examples;

import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class WebTestCase {

    private Server server;    
    
    @Before
    public void startServer() throws Exception {
        server = new Server(8080);               

        Context root = new Context(server, "/", Context.SESSIONS);
        root.addServlet(new ServletHolder(new DemoServlet()), "/");
        server.start();             
    }
    
    @After
    public void stopServer() throws Exception {
        server.stop();
    }    
    
}
