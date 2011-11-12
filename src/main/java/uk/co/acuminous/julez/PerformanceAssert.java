package uk.co.acuminous.julez;

import static org.junit.Assert.assertTrue;

public class PerformanceAssert {

    public static void assertThroughput(int expected, int actual) {
        assertTrue(String.format("Actual throughput: %d scenarios per second", actual), actual >= expected);
    }
    
    public static void assertMinPasses(int expected, int actual) {
        assertTrue(String.format("Recorded %d passes", actual), actual >= expected);        
    }
    
    public static void assertMaxFailures(int expected, int actual) {
        assertTrue(String.format("Recorded %d failures", actual), actual <= expected);        
    }    
}
