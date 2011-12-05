package uk.co.acuminous.julez.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import uk.co.acuminous.julez.util.StringUtils;

public class StringUtilsTest {

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })    
    public void joinsCollections() {
        List items = Arrays.asList(1, "A", 1.2D);
        assertEquals("1A1.2", StringUtils.join(items));
    } 

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })    
    public void joinsCollectionsUsingSeparator() {
        List items = Arrays.asList(1, "A", 1.2D);
        assertEquals("1,A,1.2", StringUtils.join(items, ","));
    }    
    
    @Test
    public void joinsEmptyCollections() {
        assertEquals("", StringUtils.join(Collections.emptySet(), ","));
    }   
        
    @Test
    public void joinTolleratesNullData() {
        assertEquals("", StringUtils.join(null, ","));
    }
    
    @Test
    @SuppressWarnings({ "rawtypes" })    
    public void joinTolleratesNullSeparator() {
        List items = Arrays.asList("foo", "bar");
        assertEquals("foobar", StringUtils.join(items, null));

    }    
}
