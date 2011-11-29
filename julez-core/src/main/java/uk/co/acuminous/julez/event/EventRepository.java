package uk.co.acuminous.julez.event;

public interface EventRepository extends EventSource, EventHandler {
    void replay();    
}
