package uk.co.acuminous.julez;

public interface ResultRecorder {

    public abstract int successCount();

    public abstract int failureCount();

    public abstract void pass();

    public abstract void pass(String description);

    public abstract void fail(String description);

}
