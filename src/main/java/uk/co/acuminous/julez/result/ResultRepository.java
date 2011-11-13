package uk.co.acuminous.julez.result;

public interface ResultRepository {

    Result get(String id);

    int count();

    void add(Result result);

    void dump();

    void dump(ResultStatus status);

}
