package uk.co.acuminous.julez.result;

import java.util.HashMap;
import java.util.Map;


public class InMemoryResultRepository implements ResultRepository {

    Map<String, Result> results = new HashMap<String, Result>();
    
    @Override
    public Result get(String id) {
        return results.get(id);
    }

    @Override
    public int count() {
        return results.size();
    }

    @Override
    public void add(Result result) {
        results.put(result.getId(), result);
    }

    @Override
    public void dump() {
        for (Result result : results.values()) {
            System.out.println(result);
        }
    }

    @Override
    public void dump(ResultStatus status) {
        for (Result result : results.values()) {
            if (status == result.getStatus()) {
                System.out.println(result);
            }
        }
    }

}
