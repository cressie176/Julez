package uk.co.acuminous.julez;

public class Result {

    enum ResultStatus { PASS, FAIL }

    long timestamp;
    ResultStatus status;
    String description;

    public Result(long timestamp, ResultStatus status, String description) {
        this.timestamp = timestamp;
        this.status = status;
        this.description = description;
    }
    
    public Result(ResultStatus status, String description) {
        this(System.currentTimeMillis(), status, description);
    }
    
    public Result(ResultStatus status) {
        this(System.currentTimeMillis(), status, null);
    }
}
