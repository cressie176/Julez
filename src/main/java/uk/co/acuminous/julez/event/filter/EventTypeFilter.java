package uk.co.acuminous.julez.event.filter;

import java.util.regex.Pattern;

import uk.co.acuminous.julez.event.BaseEventSource;
import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public class EventTypeFilter extends BaseEventSource implements EventHandler {

    public Pattern pattern;
    
    public EventTypeFilter(String pattern) {
        this.pattern = Pattern.compile(pattern);        
    }
    
    @Override
    public void onEvent(Event<?> event) {
        if (pattern.matcher(event.getType()).matches()) {
            raise(event);
        }
    }

}
