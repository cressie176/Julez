package uk.co.acuminous.julez.scenario;

import java.util.List;

import junit.framework.Assert;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;


public class ScenarioSteps {

    private final List<String> recorder;

    public ScenarioSteps(List<String> recorder) {
        this.recorder = recorder;
    }
    
    @Given("some precondition")
    public void one() {
        recorder.add("given");
    }
    
    @When("I perform some action")
    public void two() {
        recorder.add("when");
    }
    
    @Then("verify some side effect")
    public void three() {
        recorder.add("then");
    }
    
    @Given("a step that throws an exception")
    public void exception() {
        throw new RuntimeException("Test Exception");
    }
    
    @Given("a failing step")
    public void fail() {
        Assert.fail("Assertion Failed");
    }
}
