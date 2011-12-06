package uk.co.acuminous.julez.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.acuminous.julez.transformer.CamelCaseSeparator;

public class CamelCaseSeparatorTest {

    @Test
    public void injectsDefaultStringBetweenCamelCasedWords() {
        CamelCaseSeparator transformer = new CamelCaseSeparator();
        assertEquals("camel Case", transformer.transform("camelCase"));
        assertEquals("camel Case Camels", transformer.transform("camelCaseCamels"));
        assertEquals("camel CASE Camels", transformer.transform("camelCASECamels"));
        assertEquals("camel 123 Case", transformer.transform("camel123Case"));
    }
    
    @Test
    public void injectsSpecifiedStringBetweenCamelCasedWords() {
        CamelCaseSeparator transformer = new CamelCaseSeparator(".");
        assertEquals("camel.Case", transformer.transform("camelCase"));
        assertEquals("camel.Case.Camels", transformer.transform("camelCaseCamels"));
        assertEquals("camel.CASE.Camels", transformer.transform("camelCASECamels"));
        assertEquals("camel.123.Case", transformer.transform("camel123Case"));
    }    
    
}
