package uk.co.acuminous.julez.event.filter;

import uk.co.acuminous.julez.event.BaseEventSource;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public class EventClassFilter<T> extends BaseEventSource implements EventHandler {

    public Class<T> filter;
    
    public EventClassFilter(Class<T> filter) {
        this.filter = filter;        
    }
    
    @Override
    public void onEvent(Event event) {
        if (filter.isAssignableFrom(event.getClass())) {
            raise(event);        
        }
    }

}
