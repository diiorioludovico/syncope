package org.apache.syncope.common.keymaster.client.zookeeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ZookeeperConfParamOpsTest {
    private static CuratorFramework client;
    private static ZookeeperConfParamOpsList confParamOps;

    @BeforeClass
    public static void configuration() {
        // definizione del client zookeeper
        client = new CuratorFramework();

        // inizializzazione dell'oggetto ZookeeperConfParamOpsList
        confParamOps = new ZookeeperConfParamOpsList(client);
    }

    @Test
    public void buildConfPathTest() {
        String domain = "domain_test";
        result = confParamOps.buildConfPathTest(domain);
        Assert.assertEquals("/conf/domain_test", result);

    }    
}