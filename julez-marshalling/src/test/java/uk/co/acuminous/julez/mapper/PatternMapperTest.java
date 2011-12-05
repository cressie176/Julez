package uk.co.acuminous.julez.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import uk.co.acuminous.julez.mapper.PatternMapper;

public class PatternMapperTest {

    private PatternMapper mapper;

    @Test
    public void matchesLiteralKeys() {
        initDefaultMapper();        
        assertEquals("Bar", mapper.getValue("Foo"));
    }
    
    @Test
    public void matchesPattern() {
        initDefaultMapper();
        assertEquals("Banana", mapper.getValue("Food"));
    }
    
    @Test
    public void providesAllValues() {
        initDefaultMapper();
        assertEquals(Arrays.asList("Bar", "Banana", "XXX", "FFF"), new ArrayList<Object>(mapper.getValues()));
    }

    private void initDefaultMapper() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("Foo", "Bar");
        map.put("F.*d", "Banana");
        map.put("X.*", "XXX");
        map.put("F.*", "FFF");
        mapper = new PatternMapper(map);
    }
    
}
