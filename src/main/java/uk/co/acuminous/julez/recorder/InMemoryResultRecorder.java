package uk.co.acuminous.julez.recorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.acuminous.julez.result.Result;
import uk.co.acuminous.julez.result.ResultFactory;
import uk.co.acuminous.julez.result.ResultStatus;

public class InMemoryResultRecorder extends BaseResultRecorder {

    protected List<Result> results = Collections.synchronizedList(new ArrayList<Result>());
    protected ResultFactory resultFactory;
    
    public InMemoryResultRecorder(ResultFactory resultFactory) {
        this.resultFactory = resultFactory;        
    }    
    
    @Override    
    public void fail(String description) {
        results.add(resultFactory.getInstance(ResultStatus.FAIL, description));
    }

    @Override
    public void pass(String description) {
        results.add(resultFactory.getInstance(ResultStatus.PASS, description));
    }

    @Override
    public int failureCount() {
        return statusCount(ResultStatus.FAIL);
    }

    @Override
    public int passCount() {
        return statusCount(ResultStatus.PASS);
    }

    private int statusCount(ResultStatus status) {
        int count = 0;
        for (Result result : results) {
            count += result.getStatus() == status ? 1 : 0;
        }
        return count;
    }

    @Override
    public void shutdownGracefully() {
        // NOOP        
    }
}
