package uk.co.acuminous.julez.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.junit.Test;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.mapper.TransformingMapper;
import uk.co.acuminous.julez.mapper.TwoWayMapper;
import uk.co.acuminous.julez.transformer.LowerCaseTransformer;

public class TransformingMapperTest {

    TwoWayMapper mapper;
    Collection<String> keys;

    @Test
    public void mapsKeysToValues() {
        initDefaultMapper();
        assertEquals(Event.ID, mapper.getKey("#id"));
        assertEquals(Event.TIMESTAMP, mapper.getKey("#timestamp"));
    }

    @Test
    public void mapsValuesToKeys() {
        initDefaultMapper();
        assertEquals("#id", mapper.getValue(Event.ID));
        assertEquals("#timestamp", mapper.getValue(Event.TIMESTAMP));
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
        assertEquals(new TreeSet<String>(keys), new TreeSet<String>(mapper.getKeys()));
    }

    @Test
    public void keysMaintainOriginalOrder() {
        initExplicitlyOrderedMapper();
        assertEquals(Arrays.asList("Z", "A", "F", "W"), new ArrayList<String>(mapper.getKeys()));
    }

    @Test
    public void providesAllValues() {
        initDefaultMapper();
        assertEquals(new TreeSet<String>(Arrays.asList("#id", "#timestamp")), new TreeSet<String>(mapper.getValues()));
    }

    @Test
    public void valuesAreConsistentWithKeyOrder() {
        initExplicitlyOrderedMapper();
        assertEquals(Arrays.asList("z", "a", "f", "w"), new ArrayList<String>(mapper.getValues()));
    }

    private void initDefaultMapper() {
        keys = new HashSet<String>();
        keys.add(Event.ID);
        keys.add(Event.TIMESTAMP);
        mapper = new TransformingMapper(new LowerCaseTransformer(), keys);
    }

    private void initExplicitlyOrderedMapper() {
        keys = new ArrayList<String>();
        keys.add("Z");
        keys.add("A");
        keys.add("F");
        keys.add("W");
        mapper = new TransformingMapper(new LowerCaseTransformer(), keys);
    }
    
}
