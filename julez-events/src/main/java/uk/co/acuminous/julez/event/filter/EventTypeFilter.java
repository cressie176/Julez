package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.Event;

public class EventTypeFilter extends EventDataFilter {

    public EventTypeFilter(String pattern) {
        super(Event.TYPE, pattern);
    }

}
