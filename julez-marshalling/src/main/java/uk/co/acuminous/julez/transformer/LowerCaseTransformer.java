package uk.co.acuminous.julez.transformer;


public class LowerCaseTransformer implements StringTransformer {

    @Override
    public String transform(String source) {
        return source.toLowerCase();
    }

}
