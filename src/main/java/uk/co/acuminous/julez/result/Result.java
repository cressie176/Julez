package uk.co.acuminous.julez.result;

import java.util.UUID;

import com.google.gson.Gson;

public class Result {

    private final String id;    
    private final String source;
    private final String run;
    private final String scenarioName;
    private final long timestamp;        
    private final ResultStatus status;
    private final String description;

    public Result(String source, String run, String scenarioName, long timestamp, ResultStatus status, String description) {
        this(UUID.randomUUID().toString(), source, run, scenarioName, timestamp, status, description);
    }
    
    public Result(String id, String source, String run, String scenarioName, long timestamp, ResultStatus status, String description) {
        this.id = id;
        this.source = source;
        this.run = run;
        this.timestamp = timestamp;
        this.scenarioName = scenarioName;        
        this.status = status;
        this.description = description;        
    }

    public String getId() {
        return id;
    }
    
    public String getSource() {
        return source;
    }

    public String getRun() {
        return run;
    }    
    
    public long getTimestamp() {
        return timestamp;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public ResultStatus getStatus() {
        return status;
    }    
   
    public String getDescription() {
        return description;
    }    

    public String toString() {
        return toJson();
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }    
    
    public static Result fromJson(String json) {
        return new Gson().fromJson(json, Result.class);
    }
}
