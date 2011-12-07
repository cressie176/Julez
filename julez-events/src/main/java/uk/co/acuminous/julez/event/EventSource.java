package uk.co.acuminous.julez.event;

public interface EventSource {
    EventSource register(EventHandler handler);
}
