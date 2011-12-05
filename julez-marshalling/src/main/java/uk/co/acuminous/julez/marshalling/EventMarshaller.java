package uk.co.acuminous.julez.marshalling;

import uk.co.acuminous.julez.event.Event;

public interface EventMarshaller {
    String marshall(Event event);
}
