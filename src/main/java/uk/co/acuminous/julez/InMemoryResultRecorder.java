package uk.co.acuminous.julez;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.acuminous.julez.Result.ResultStatus;

public class InMemoryResultRecorder implements ResultRecorder {

    protected List<Result> results = Collections.synchronizedList(new ArrayList<Result>());

    @Override    
    public void fail(String description) {
        results.add(new Result(ResultStatus.FAIL, description));
        System.err.println(description);
    }

    @Override
    public void pass(String description) {
        results.add(new Result(ResultStatus.PASS, description));
    }

    @Override
    public void pass() {
        results.add(new Result(ResultStatus.PASS));
    }

    @Override
    public int failureCount() {
        return statusCount(ResultStatus.FAIL);
    }

    @Override
    public int successCount() {
        return statusCount(ResultStatus.PASS);
    }

    private int statusCount(ResultStatus status) {
        int count = 0;
        for (Result result : results) {
            count += result.status == status ? 1 : 0;
        }
        return count;
    }

}
