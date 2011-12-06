package uk.co.acuminous.julez.transformer;

import uk.co.acuminous.julez.transformer.CamelCaseSeparator;
import uk.co.acuminous.julez.transformer.MultiTransformer;
import uk.co.acuminous.julez.transformer.SubstitutionTransformer;
import uk.co.acuminous.julez.transformer.UpperCaseTransformer;

public class DefaultColumnNameTransformer extends MultiTransformer {

    public DefaultColumnNameTransformer() {
        super(new SubstitutionTransformer("^[^A-Za-z\\d]+|[^A-Za-z\\d]+$"), 
              new SubstitutionTransformer("[\\W]", "_"), 
              new CamelCaseSeparator("_"), 
              new UpperCaseTransformer(), 
              new SubstitutionTransformer("_+", "_"));        
    }
    
}
