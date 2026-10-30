package org.apache.syncope.common.keymaster.client.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModeStatPathAndBytesable;
import org.apache.syncope.common.keymaster.client.api.KeymasterException;
import org.apache.zookeeper.data.Stat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

/*
    TEST CLASS for ZookeeperConfParamOps
*/

@ExtendWith(MockitoExtension.class)
class ZookeeperConfParamOpsTest {
    private static ZookeeperConfParamOps confParamOps;

    @Mock
    private CuratorFramework client;

    @Mock
    private ExistsBuilder existsBuilder;

    @Mock
    private CreateBuilder createBuilder;

    @Mock
    private ProtectACLCreateModeStatPathAndBytesable<String> createPathBuilder ;

    @Mock
    private GetChildrenBuilder childrenBuilder;

    @Mock
    private Stat stat;

    @Mock
    private GetDataBuilder getDataBuilder;


    @BeforeEach
    public void configuration() {
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
    public void listDontExistTest() throws Exception {
        String domain = "domain";
        String path = ZookeeperConfParamOps.buildConfPath(domain);        
        
        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // viene creato il nuovo dominio
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder );
        when(createPathBuilder .forPath(path)).thenReturn(null);
        
        // non trova figli, quindi serve una mock per client.getChildren() che restituisce una lista di stringhe vuota
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of());

        Map<String, Object> result = confParamOps.list(domain);
        
        assertTrue(result.isEmpty());        
    }

    @Test
    public void listExistTest() throws Exception {
        String domain = "domain";
        String path = ZookeeperConfParamOps.buildConfPath(domain);    

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca qualcosa
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(stat);

        // serve una mock per client.getChildren() che restituisce qualcosa
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of("maxRetries", "expireTime"));
        
        // viene popolata la mappa
        when(client.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(path + "/maxRetries")).thenReturn("3".getBytes());
        when(getDataBuilder.forPath(path + "/expireTime")).thenReturn("60".getBytes());
        
        // viene restituita la mappa piena
        Map<String, Object> result = confParamOps.list(domain);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(3, result.get("maxRetries"));
        assertEquals(60, result.get("expireTime"));
    }

    @Test
    public void listInvalidZookeeperDomainTest() throws Exception{
        String domain = "zookeeper";
        String path = ZookeeperConfParamOps.buildConfPath(domain); 

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // tenta di creare il nuovo dominio, ma genera eccezione
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder );
        when(createPathBuilder .forPath(path)).thenThrow(new Exception("ZooKeeper rejected reserved domain"));

        // verifichiamo che l'eccezione sia stata generata
        assertThrows(KeymasterException.class, () -> confParamOps.list(domain));
    }

    @Test
    public void listEmptyDomainTest() throws Exception {
        String domain = "";
        String path = ZookeeperConfParamOps.buildConfPath(domain); 
        
        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // tenta di creare il nuovo dominio, ma genera eccezione
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder.forPath(path)).thenThrow(new IllegalArgumentException ("Invalid path")); 

        //IllegalArgumentException rappresenta l’errore che Curator solleva per un path ZooKeeper malformato

        assertThrows(KeymasterException.class, () -> confParamOps.list(domain));
    }

    @Test
    public void listNullDomainTest() throws Exception {
        String domain = null;
        String path = ZookeeperConfParamOps.buildConfPath(domain); // "/conf/null"

        // serve una mock su client per la funzione client.checkExists() in modo che restituisca null
        when(client.checkExists()).thenReturn(existsBuilder);
        when(existsBuilder.forPath(path)).thenReturn(null);

        // viene creato il dominio "/conf/null"
        when(client.create()).thenReturn(createBuilder);
        when(createBuilder.creatingParentContainersIfNeeded()).thenReturn(createPathBuilder);
        when(createPathBuilder.forPath(path)).thenReturn(path);

        // si recuperano le configurazioni sotto tale dominio, ma non viene restituito nulla
        when(client.getChildren()).thenReturn(childrenBuilder);
        when(childrenBuilder.forPath(path)).thenReturn(List.of());

        Map<String, Object> result = confParamOps.list(domain);

        assertTrue(result.isEmpty());
    }
}