package org.apache.syncope.common.keymaster.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/*
    TEST CLASS for ZookeeperConfParamOps
*/

class ZookeeperConfParamOpsTest {
    private static CuratorFramework client;
    private static ZookeeperConfParamOps confParamOps;

    @BeforeAll
    public static void configuration() {
        // definizione del client zookeeper
        client = null;

        // inizializzazione dell'oggetto ZookeeperConfParamOps
        confParamOps = new ZookeeperConfParamOps(client);
    }

    /* _____________________________________
       |                                    |
       |    test per buildConfPathTest()    |
       |____________________________________|   */
    
    @Test
    public void buildConfPathTest() {
        String domain = "domain_test";
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/domain_test", result);
    }    

    @Test
    public void buildConfPathMultipleParametersTest() {
        String domain = "domain";
        String test = "test";
        String result = ZookeeperConfParamOps.buildConfPath(domain, test);
        assertEquals("/conf/domain/test", result);
    }    

    @Test
    public void buildConfPathEmptyStringTest() {
        String domain = "";
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/", result);
    } 
    
    @Test
    public void buildConfPathNullStringTest() {
        String domain = null;
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/null", result);
    } 

    /* _____________________________________
       |                                    |
       |         test per list()            |
       |____________________________________|   */

    @Test
    public void listExceptionTest() {
        String domain = "domain";
        assertThrows(KeymasterException.class, () -> confParamOps.list(domain));
    }
}