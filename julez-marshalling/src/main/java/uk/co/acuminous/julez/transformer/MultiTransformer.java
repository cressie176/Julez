package uk.co.acuminous.julez.transformer;


public class MultiTransformer implements StringTransformer {

    private final StringTransformer[] transformers;

    public MultiTransformer(StringTransformer... transformers) {
        this.transformers = transformers;        
    }

    @Override
    public String transform(String source) {
        for (StringTransformer transformer : transformers) {
            source = transformer.transform(source);
        }
        return source;
    }
    
}
