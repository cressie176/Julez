package uk.co.acuminous.julez.event;

public interface EventSource {
    void registerEventHandler(EventHandler... handlers);
}
