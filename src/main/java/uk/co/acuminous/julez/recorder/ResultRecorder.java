package uk.co.acuminous.julez.recorder;

import uk.co.acuminous.julez.ResultSink;

public interface ResultRecorder extends ResultSink, ResultExaminer {
    int passCount();
    int failureCount();
    int percentage();    
    void pass();
    void pass(String description);
    void fail(String description);
    void shutdownGracefully();
    void complete(int timeout);
}
