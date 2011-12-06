package uk.co.acuminous.julez.event.filter;

import java.util.regex.Pattern;

import uk.co.acuminous.julez.event.Event;

public class EventDataFilter extends BaseEventFilter {

    private final String key;    
    private final Pattern pattern;

    public EventDataFilter(String key, String pattern) {
        this.key = key;
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean accept(Event event) {
        return pattern.matcher(event.get(key)).matches();
    }
}
