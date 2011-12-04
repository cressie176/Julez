package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;

public abstract class BaseEventFilter extends PassThroughPipe {

    @Override
    public void onEvent(Event event) {
        if (accept(event)) super.onEvent(event);
    }

    public abstract boolean accept(Event event);

}
