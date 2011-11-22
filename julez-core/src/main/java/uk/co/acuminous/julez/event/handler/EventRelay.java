package uk.co.acuminous.julez.event.handler;

import uk.co.acuminous.julez.event.BaseEventSource;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public class EventRelay extends BaseEventSource implements EventHandler {

    @Override
    public void onEvent(Event event) {
        raise(event);        
    }

}
