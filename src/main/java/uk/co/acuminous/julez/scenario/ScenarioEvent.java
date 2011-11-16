package uk.co.acuminous.julez.scenario;

public class ScenarioEvent {

    public static final String START = "START";
    public static final String PASS = "PASS";
    public static final String FAIL = "FAIL";
    
    private String type;
    private long timestamp;

    public static ScenarioEvent start() {
        return new ScenarioEvent(START);
    }    
    
    public static ScenarioEvent pass() {
        return new ScenarioEvent(PASS);
    }
    
    public static ScenarioEvent fail() {
        return new ScenarioEvent(FAIL);
    }    
    
    public ScenarioEvent(String type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
