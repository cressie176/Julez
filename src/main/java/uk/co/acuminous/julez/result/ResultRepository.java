package uk.co.acuminous.julez.result;

public interface ResultRepository {

    public abstract Result get(String id);

    public abstract int count();

    public abstract void add(Result result);

    public abstract void dump();

}
