package uk.co.acuminous.julez.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.transformer.LowerCaseTransformer;
import uk.co.acuminous.julez.transformer.MultiTransformer;
import uk.co.acuminous.julez.transformer.SubstitutionTransformer;

public class MultiTransformerTest {

    @Test
    public void appliesMultipleTransformations() {
        MultiTransformer transformer = new MultiTransformer(new LowerCaseTransformer(), new SubstitutionTransformer("[\\W]", ""));
        assertEquals("time_stamp", transformer.transform("#TIME_STAMP"));
    }
    
}
