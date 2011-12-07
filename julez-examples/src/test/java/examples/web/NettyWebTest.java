package examples.web;

import static org.jboss.netty.channel.Channels.pipeline;
import static uk.co.acuminous.julez.runner.ScenarioRunner.ConcurrencyUnit.THREADS;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import uk.co.acuminous.julez.event.handler.ThroughputMonitor;
import uk.co.acuminous.julez.runner.ConcurrentScenarioRunner;
import uk.co.acuminous.julez.scenario.BaseScenario;
import uk.co.acuminous.julez.scenario.ScenarioEvent;
import uk.co.acuminous.julez.scenario.ScenarioSource;
import uk.co.acuminous.julez.scenario.source.SizedScenarioRepeater;
import uk.co.acuminous.julez.test.WebTestCase;

public class NettyWebTest extends WebTestCase {

    @Test
    public void demonstrateAConcurrentWebTestUsingNetty() {

        NettyScenario scenario = new NettyScenario();
        ScenarioSource scenarios = new SizedScenarioRepeater(scenario, 100);

        ThroughputMonitor throughputMonitor = new ThroughputMonitor();
        scenario.register(throughputMonitor);

        ConcurrentScenarioRunner runner = new ConcurrentScenarioRunner();
        runner.register(throughputMonitor);
        runner.queue(scenarios).allocate(10, THREADS).go();

        System.out.println("\nNetty Throughput\n----------------");
        System.out.println(throughputMonitor.getThroughput());
    }

    class NettyScenario extends BaseScenario {
               
        @Override
        public void run() {
            onEvent(eventFactory.begin());
            HttpClient client = new HttpClient(this);            
            try
            {   
                client.init();                
                client.get("http://localhost:8081/");
            } catch (Exception e) {
                error(e.getMessage());
            } finally {
                client.shutdown();
            }
            onEvent(eventFactory.end());
        }
                
        public void pass() {
            onEvent(eventFactory.pass());
        }
        
        public void fail(Integer status, String message) {
            ScenarioEvent event = eventFactory.fail();
            event.getData().put("statusCode", String.valueOf(status));
            event.getData().put("message", message);
            onEvent(event);
        }
        
        public void error(String message) {
            ScenarioEvent event = eventFactory.error();
            event.getData().put("message", message);
            onEvent(event);       
        }        
    }

    class HttpClient {

        private String host = "localhost";
        private int port = 8081;
        private Channel channel;
        private ClientBootstrap bootstrap;
        private final NettyScenario scenario;

        public HttpClient(NettyScenario scenario) {
            this.scenario = scenario;
        }

        public void init() {
            bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
            bootstrap.setPipelineFactory(new HttpClientPipelineFactory(scenario));

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

            channel = future.awaitUninterruptibly().getChannel();
            
            if (!future.isSuccess()) {
                future.getCause().printStackTrace();
                bootstrap.releaseExternalResources();
                return;
            }
        }

        public void get(String uri) {
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
            request.setHeader(HttpHeaders.Names.HOST, host);
            request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            channel.write(request);
            channel.getCloseFuture().awaitUninterruptibly();
        }

        public void shutdown() {
            bootstrap.releaseExternalResources();
        }
    }

    class HttpClientPipelineFactory implements ChannelPipelineFactory {

        private final NettyScenario scenario;

        public HttpClientPipelineFactory(NettyScenario scenario) {
            this.scenario = scenario;
        }

        public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline pipeline = pipeline();
            pipeline.addLast("codec", new HttpClientCodec());
            pipeline.addLast("handler", new HttpResponseHandler(scenario));
            return pipeline;
        }
    }

    class HttpResponseHandler extends SimpleChannelUpstreamHandler {

        private boolean readingChunks;
        private final NettyScenario scenario;

        public HttpResponseHandler(NettyScenario scenario) {
            this.scenario = scenario;
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            if (!readingChunks) {
                HttpResponse response = (HttpResponse) e.getMessage();

                if (response.getStatus().getCode() == 200) {
                     scenario.pass();                    
                } else {
                     scenario.fail(response.getStatus().getCode(), response.getStatus().getReasonPhrase());
                }

                if (response.isChunked()) {
                    readingChunks = true;
                }
            }
        }
    }
}