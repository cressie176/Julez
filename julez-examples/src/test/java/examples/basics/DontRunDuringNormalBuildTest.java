package examples.basics;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DontRunDuringNormalBuildTest {

    @Before
    public void init() throws UnknownHostException {        
        Set<String> testHosts = new HashSet<String>(Arrays.asList("rod", "jane", "freddy"));
        assumeTrue(testHosts.contains(InetAddress.getLocalHost().getHostName()));
    }
    
    @Test
    public void demonstrateHowToRestrictTestsToCertainClientMachines() {
        fail("This test will only run on machines called rod, jane or freddy");
    }
    
}
