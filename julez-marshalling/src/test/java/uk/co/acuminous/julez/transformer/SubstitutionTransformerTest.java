package uk.co.acuminous.julez.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.transformer.SubstitutionTransformer;

public class SubstitutionTransformerTest {

    @Test
    public void substitutesMatchingCharacters() {
        SubstitutionTransformer replacer = new SubstitutionTransformer("[\\W]", ".");
        assertEquals(".TI.ME_ST.AMP", replacer.transform("#TI+ME_ST-AMP"));
    }
}
