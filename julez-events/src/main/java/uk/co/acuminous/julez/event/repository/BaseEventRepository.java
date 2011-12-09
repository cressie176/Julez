package uk.co.acuminous.julez.event.repository;

import java.util.List;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.filter.EventFilter;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;

public abstract class BaseEventRepository extends PassThroughPipe implements FilteringEventRepository {
    
    public int count(EventFilter filter) {
        return list(filter).size();
    }

    public List<Event> list(EventFilter filter) {
        return filter.applyTo(list());
    }
}
