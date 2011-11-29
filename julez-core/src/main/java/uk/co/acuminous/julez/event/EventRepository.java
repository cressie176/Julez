package uk.co.acuminous.julez.event;

public interface EventRepository extends EventPipe {
    void replay();    
}
