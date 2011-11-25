package uk.co.acuminous.julez.plumbing;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventPipe;
import uk.co.acuminous.julez.event.EventSource;

public abstract class BaseEventPipe implements EventPipe {
	
	private EventHandler handler;

    @Override public EventSource register(EventHandler handler) {
        this.handler = handler;
        return this;
    }
    
    @Override public void onEvent(Event event) {
    	if (null != handler) handler.onEvent(event);
    }
}
