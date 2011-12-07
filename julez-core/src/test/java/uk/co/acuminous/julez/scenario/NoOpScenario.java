package uk.co.acuminous.julez.scenario;

public class NoOpScenario extends BaseScenario {

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());
        handler.onEvent(eventFactory.end());
    }
}
