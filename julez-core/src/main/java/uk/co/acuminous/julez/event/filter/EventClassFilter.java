package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.Event;

public class EventClassFilter<T> extends BaseEventFilter {

    private final Class<T> filter;

    public EventClassFilter(Class<T> filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(Event event) {
        return filter.isAssignableFrom(event.getClass());
    }

}
