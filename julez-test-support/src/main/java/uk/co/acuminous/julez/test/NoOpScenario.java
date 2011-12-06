package uk.co.acuminous.julez.test;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class NoOpScenario extends BaseScenario {

    @Override
    public void run() {
        onEvent(eventFactory.begin());
        onEvent(eventFactory.end());
    }
}
