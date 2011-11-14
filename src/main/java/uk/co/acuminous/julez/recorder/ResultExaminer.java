package uk.co.acuminous.julez.recorder;

public interface ResultExaminer {
    int passCount();
    int failureCount();
    int percentage(); 
}
