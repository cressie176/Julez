package uk.co.acuminous.julez.transformer;



public class CamelCaseTransformer extends SplitTransformer {

    public CamelCaseTransformer() {
        this("_");
    }
    
    public CamelCaseTransformer(String separator) {
        super("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])", separator);
    }

}
