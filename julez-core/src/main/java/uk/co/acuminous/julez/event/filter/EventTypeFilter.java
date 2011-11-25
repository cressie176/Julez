package uk.co.acuminous.julez.event.filter;

import java.util.regex.Pattern;

import uk.co.acuminous.julez.event.Event;

public class EventTypeFilter extends BaseEventFilter {

    public Pattern pattern;
    
    public EventTypeFilter(String pattern) {
        this.pattern = Pattern.compile(pattern);        
    }
    
    @Override public boolean accept(Event event) {
        return pattern.matcher(event.getType()).matches();
    }

}
