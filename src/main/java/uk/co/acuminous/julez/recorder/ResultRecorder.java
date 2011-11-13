package uk.co.acuminous.julez.recorder;

public interface ResultRecorder {
    int passCount();
    int failureCount();
    int percentage();    
    void pass();
    void pass(String description);
    void fail(String description);
    void shutdownGracefully();
    void complete(int timeout);
}
