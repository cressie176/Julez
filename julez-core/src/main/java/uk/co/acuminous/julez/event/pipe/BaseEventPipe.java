package uk.co.acuminous.julez.event.pipe;

import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventPipe;

public abstract class BaseEventPipe implements EventPipe {

    protected EventHandler handler;

    @Override
    public void register(EventHandler handler) {
        this.handler = handler;
    }
}
