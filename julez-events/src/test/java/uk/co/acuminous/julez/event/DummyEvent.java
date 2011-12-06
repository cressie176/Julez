package uk.co.acuminous.julez.event;

public class DummyEvent extends Event {

    public DummyEvent() {
        super("test");
    }
    
    public DummyEvent(String type) {
        super(type);
    }
    
}
