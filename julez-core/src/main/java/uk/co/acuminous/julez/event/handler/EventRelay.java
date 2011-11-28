package uk.co.acuminous.julez.event.handler;

import uk.co.acuminous.julez.event.BaseEventSource;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventPipe;

public class EventRelay extends BaseEventSource implements EventPipe {

    @Override
    public void onEvent(Event event) {
        raise(event);        
    }

}
