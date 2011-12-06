package uk.co.acuminous.julez.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DefaultColumnNameTransformerTest {

    private DefaultColumnNameTransformer transformer;

    @Before
    public void init() {
        transformer = new DefaultColumnNameTransformer();
    }

    @Test
    public void removesNonAlphaNumericCharactersFromStartAndEnd() {
        assertEquals("TESTRUN", transformer.transform("#TESTRUN"));
        assertEquals("TESTRUN", transformer.transform("TESTRUN_"));
        assertEquals("TESTRUN", transformer.transform("__TESTRUN##"));
    }

    @Test
    public void replacesNonWordCharactersWithAnUnderscore() {
        assertEquals("TEST_RUN", transformer.transform("TEST.RUN"));
        assertEquals("TEST_RUN", transformer.transform("TEST_RUN"));
        assertEquals("TEST_RUN", transformer.transform("TEST RUN"));
        assertEquals("TEST_RUN", transformer.transform("TEST .RUN"));
    }

    @Test
    public void separatesCamelCaseWithAnUnderscore() {
        assertEquals("TEST_RUN", transformer.transform("testRun"));
        assertEquals("TEST_RUN", transformer.transform("TestRun"));
        assertEquals("TEST_RUN_1", transformer.transform("TestRun1"));
        assertEquals("TEST_RUN_123", transformer.transform("TestRun123"));
    }

    @Test
    public void convertsToUpperCase() {
        assertEquals("TESTRUN", transformer.transform("testrun"));
        assertEquals("TEST_RUN", transformer.transform("testRun"));
        assertEquals("TEST_RUN", transformer.transform("TestRun"));
    }
}
