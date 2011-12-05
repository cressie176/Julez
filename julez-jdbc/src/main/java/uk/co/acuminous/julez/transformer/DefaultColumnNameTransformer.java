package uk.co.acuminous.julez.transformer;

import uk.co.acuminous.julez.transformer.CamelCaseTransformer;
import uk.co.acuminous.julez.transformer.MultiTransformer;
import uk.co.acuminous.julez.transformer.SubstitutionTransformer;
import uk.co.acuminous.julez.transformer.UpperCaseTransformer;

public class DefaultColumnNameTransformer extends MultiTransformer {

    public DefaultColumnNameTransformer() {
        super(new SubstitutionTransformer("[\\W]"), new CamelCaseTransformer(), new UpperCaseTransformer());        
    }
    
}
