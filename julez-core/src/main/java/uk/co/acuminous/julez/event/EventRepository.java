package uk.co.acuminous.julez.event;

import java.util.List;

public interface EventRepository extends EventSource {
    List<Event> list();
    void raise();    
    int count();
}
