package examples.jbehave;

public class Calculator {

    private int total;
    
    public void add(Integer n1, Integer n2) {
        total = n1 + n2;        
    }

    public void subtract(Integer n1, Integer n2) {
        total = n2 - n1;
    }

    public void multiply(Integer n1, Integer n2) {
        total = n1 * n2;
    }

    public void divide(Integer n1, Integer n2) {
        total = n1 / n2;
    }

    public int getTotal() {        
        return total;
    }

}
