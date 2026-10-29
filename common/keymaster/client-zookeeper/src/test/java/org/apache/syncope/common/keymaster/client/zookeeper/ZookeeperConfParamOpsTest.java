package org.apache.syncope.common.keymaster.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void buildConfPathTest() {
        String domain = "domain_test";
        String result = ZookeeperConfParamOps.buildConfPath(domain);
        assertEquals("/conf/domain_test", result);

    }    
}