package uk.co.acuminous.julez.scenario.source;

import java.util.LinkedList;

import uk.co.acuminous.julez.scenario.Scenario;

public class ScenarioQueue extends LinkedList<Scenario> implements ScenarioSource {

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
