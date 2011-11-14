package uk.co.acuminous.julez;

public interface ResultSink {
    void pass();
    void pass(String description);
    void fail(String description);
    void shutdownGracefully();
    void complete(int timeout);
}
