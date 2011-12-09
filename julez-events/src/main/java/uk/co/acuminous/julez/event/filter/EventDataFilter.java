package uk.co.acuminous.julez.event.filter;

import java.util.regex.Pattern;

import uk.co.acuminous.julez.event.Event;

public class EventDataFilter extends BaseEventFilter {

    private String key;    
    private Pattern pattern;

    public EventDataFilter() {        
    }
    
    public EventDataFilter(String key, String pattern) {
        this.key = key;
        this.pattern = Pattern.compile(pattern);
    }

    public EventDataFilter filterEventsWhere(String key) {
        this.key = key;
        return this;
    }
    
    public EventDataFilter matches(String pattern) {
        this.pattern = Pattern.compile(pattern);
        return this;
    }
    
    @Override
    public boolean accept(Event event) {
        return pattern.matcher(event.get(key)).matches();
    }
}
