package uk.co.acuminous.julez.util;

import static org.junit.Assert.assertTrue;

public class PerformanceAssert {

    public static void assertMinimumThroughput(int expected, int actual) {
        assertTrue(String.format("Actual throughput: %d scenarios per second", actual), actual >= expected);
    }
    
    public static void assertMinimumPasses(int expected, int actual) {
        assertTrue(String.format("Recorded %d passes", actual), actual >= expected);        
    }
    
    public static void assertMaximumFailures(int expected, int actual) {
        assertTrue(String.format("Recorded %d failures", actual), actual <= expected);        
    }

    public static void assertPassMark(int expected, int actual) {
        assertTrue(String.format("Recorded %d percentage", actual), actual >= expected);        
        
    }    
}
