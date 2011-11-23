package uk.co.acuminous.julez.event;

public interface EventSource {
    void register(EventHandler... handler);
}
