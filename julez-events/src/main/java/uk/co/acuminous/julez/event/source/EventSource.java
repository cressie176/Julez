package uk.co.acuminous.julez.event.source;

import uk.co.acuminous.julez.event.handler.EventHandler;

public interface EventSource {
    EventSource register(EventHandler handler);
}
