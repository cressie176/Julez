package uk.co.acuminous.julez.event.repository;

import uk.co.acuminous.julez.event.handler.EventHandler;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;

public abstract class BaseEventRepository extends PassThroughPipe implements EventRepository {
    
    @Override
    public BaseEventRepository register(EventHandler handler) {
        super.register(handler);
        return this;
    }

}
