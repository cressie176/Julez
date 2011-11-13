package uk.co.acuminous.julez.recorder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import uk.co.acuminous.julez.result.Result;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.result.ResultStatus;


public class DefaultResultFactory implements ResultFactory {

    private final String source;
    private final String run;
    private final String scenario;    
    
    public DefaultResultFactory(String scenarioName) {
        this(UUID.randomUUID().toString(), scenarioName);
    }
    
    public DefaultResultFactory(String run, String scenario) {
        this.source = getSource();
        this.run = run;
        this.scenario = scenario;                
    }
    
    public DefaultResultFactory(String source, String run, String scenario) {
        this.source = source;
        this.run = run;
        this.scenario = scenario;
    }
    
    @Override    
    public Result getInstance(ResultStatus status, String description) {
        return new Result(source, run, scenario, System.currentTimeMillis(), status, description);
    }

    @Override
    public Result getInstance(ResultStatus status) {
        return getInstance(status, "");
    }
        
    private String getSource() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }        
    }    
}
