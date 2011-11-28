package uk.co.acuminous.julez.scenario;

public class NoOpScenario extends BaseScenario {

    @Override
    public void run() {
        raise(eventFactory.begin());
        raise(eventFactory.end());
    }
}
