package uk.co.acuminous.julez.event.pipe;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.test.TestEventRepository;

public class PassThroughEventPipeTest {

    @Test
    public void forwardsEventsToRegisteredHandler() {

        TestEventRepository repository = new TestEventRepository();

        PassThroughEventPipe pipe = new PassThroughEventPipe().register(repository);

        Event event = new Event("test");
        pipe.onEvent(event);

        repository.assertEvents(event);
    }

    @Test
    public void tolleratesNoHandlers() {
        new PassThroughEventPipe().onEvent(new Event("test"));
    }
}
