package uk.co.acuminous.julez.scenario;

import junit.framework.AssertionFailedError;

import org.jbehave.core.embedder.SilentEmbedderMonitor;

import uk.co.acuminous.julez.event.Event;

public class EventRaisingEmbedderMonitor extends SilentEmbedderMonitor {
    
    private final ScenarioEventFactory eventFactory;
    private Event event;
    
    public EventRaisingEmbedderMonitor(ScenarioEventFactory eventFactory) {
        super(null);
        this.eventFactory = eventFactory;                
    }

    @Override
    public void storyFailed(String path, Throwable cause) {
        if (AssertionFailedError.class.isAssignableFrom(cause.getClass())) {
            event = eventFactory.fail();
        } else {
            event = eventFactory.error();            
        }       
        event.getData().put("message", cause.getMessage());
    }
    
    public boolean receivedEvent() {
        return event != null;
    }
    
    public Event getEvent() {
        return event;
    }
}
