package uk.co.acuminous.julez.transformer;


public class UpperCaseTransformer implements StringTransformer {

    @Override
    public String transform(String source) {
        return source.toUpperCase();
    }

}
