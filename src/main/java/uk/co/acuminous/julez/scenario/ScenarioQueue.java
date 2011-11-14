package uk.co.acuminous.julez.scenario;

import java.util.LinkedList;

public class ScenarioQueue extends LinkedList<Scenario> implements Scenarios {

    private static final long serialVersionUID = 1L;

    @Override
    public Scenario next() {
        return remove();
    }

    @Override
    public int available() {
        return size();
    }


}
