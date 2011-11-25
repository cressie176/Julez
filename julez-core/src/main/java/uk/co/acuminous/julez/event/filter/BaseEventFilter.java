package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.plumbing.BaseEventPipe;

public abstract class BaseEventFilter extends BaseEventPipe {
    @Override public void onEvent(Event event) {
        if (accept(event)) super.onEvent(event);
    }
    
    public abstract boolean accept(Event event);

}
