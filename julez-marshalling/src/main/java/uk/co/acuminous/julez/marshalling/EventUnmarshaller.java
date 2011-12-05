package uk.co.acuminous.julez.marshalling;

import uk.co.acuminous.julez.event.Event;

public interface EventUnmarshaller {
    public Event unmarshall(String s);
}
