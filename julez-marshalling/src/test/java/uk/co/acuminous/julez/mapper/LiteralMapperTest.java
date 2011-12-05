package uk.co.acuminous.julez.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.mapper.LiteralMapper;
import uk.co.acuminous.julez.mapper.TwoWayMapper;

public class LiteralMapperTest {

    TwoWayMapper mapper;
    Map<String, String> map;

    @Test
    public void mapsKeysToValues() {
        initDefaultMapper();
        assertEquals(Event.ID, mapper.getKey("id"));
        assertEquals(Event.TIMESTAMP, mapper.getKey("timestamp"));
    }

    @Test
    public void mapsValuesToKeys() {
        initDefaultMapper();
        assertEquals("id", mapper.getValue(Event.ID));
        assertEquals("timestamp", mapper.getValue(Event.TIMESTAMP));
    }

    @Test
    public void tolleratesMissingMappings() {
        initDefaultMapper();
        assertNull(mapper.getValue("DOES NOT EXIST"));
        assertNull(mapper.getKey("DOES NOT EXIST"));
    }

    @Test
    public void providesAllKeys() {
        initDefaultMapper();
        assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(mapper.getKeys()));
    }

    @Test
    public void keysMaintainOriginalOrder() {
        initExplicitlyOrderedMapper();
        assertEquals(Arrays.asList("Z", "A", "F", "W"), new ArrayList<String>(mapper.getKeys()));
    }

    @Test
    public void providesAllValues() {
        initDefaultMapper();
        assertEquals(new TreeSet<String>(map.values()), new TreeSet<String>(mapper.getValues()));
    }

    @Test
    public void valuesAreConsistentWithKeyOrder() {
        initExplicitlyOrderedMapper();
        assertEquals(Arrays.asList("z", "a", "f", "w"), new ArrayList<String>(mapper.getValues()));
    }

    private void initDefaultMapper() {
        map = new HashMap<String, String>();
        map.put(Event.ID, "id");
        map.put(Event.TIMESTAMP, "timestamp");
        mapper = new LiteralMapper(map);
    }

    private void initExplicitlyOrderedMapper() {
        map = new LinkedHashMap<String, String>();
        map.put("Z", "z");
        map.put("A", "a");
        map.put("F", "f");
        map.put("W", "w");
        mapper = new LiteralMapper(map);
    }

}
