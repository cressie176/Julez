package uk.co.acuminous.julez.scenario.instruction;

import uk.co.acuminous.julez.scenario.BaseScenario;

public class NoOpScenario extends BaseScenario {

    @Override
    public void run() {
        handler.onEvent(eventFactory.begin());
        handler.onEvent(eventFactory.end());
    }
}