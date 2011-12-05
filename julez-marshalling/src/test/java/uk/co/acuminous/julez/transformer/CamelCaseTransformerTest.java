package uk.co.acuminous.julez.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.transformer.CamelCaseTransformer;

public class CamelCaseTransformerTest {

    @Test
    public void injectsDefaultStringBetweenCamelCasedWords() {
        CamelCaseTransformer transformer = new CamelCaseTransformer();
        assertEquals("camel_Case", transformer.transform("camelCase"));
        assertEquals("camel_Case_Camels", transformer.transform("camelCaseCamels"));
        assertEquals("camel_CASE_Camels", transformer.transform("camelCASECamels"));
        assertEquals("camel_123_Case", transformer.transform("camel123Case"));
    }
    
    @Test
    public void injectsSpecifiedStringBetweenCamelCasedWords() {
        CamelCaseTransformer transformer = new CamelCaseTransformer(".");
        assertEquals("camel.Case", transformer.transform("camelCase"));
        assertEquals("camel.Case.Camels", transformer.transform("camelCaseCamels"));
        assertEquals("camel.CASE.Camels", transformer.transform("camelCASECamels"));
        assertEquals("camel.123.Case", transformer.transform("camel123Case"));
    }    
    
}
