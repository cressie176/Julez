package uk.co.acuminous.julez.result;

public interface ResultFactory {
    Result getInstance(ResultStatus status, String description);
    Result getInstance(ResultStatus status);        
}
