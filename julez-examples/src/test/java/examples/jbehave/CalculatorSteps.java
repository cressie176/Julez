package examples.jbehave;

import static junit.framework.Assert.assertEquals;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class CalculatorSteps {

    private Calculator calculator;
    
    @Given("a calculator")
    public void newCalculator() {
        calculator = new Calculator();
    }
    
    @When("I add $n1 to $n2")
    public void add(int n1, int n2) {        
        calculator.add(n1, n2);
    }
    
    @When("I subtract $n1 from $n2")
    public void substract(int n1, int n2) {
        calculator.subtract(n1, n2);
    }

    @When("I multiply $n1 by $n2")
    public void multiply(int n1, int n2) {
        calculator.multiply(n1, n2);
    } 
    
    @When("I divide $n1 by $n2")
    public void divide(int n1, int n2) {
        calculator.divide(n1, n2);
    }    
    
    @Then("I get $total")
    public void total(int total) {
        assertEquals(total, calculator.getTotal());
    }
}
