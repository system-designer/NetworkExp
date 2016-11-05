package com.raylew.network.test;

import com.raylew.network.snmp.SNMPManager;
import org.junit.Test;

/**
 * Created by Raymond on 2016/10/26.
 */
public class SNMPTest {
    @Test
    public void testGet() {
        SNMPManager snmpManager = new SNMPManager();
        String oidName = snmpManager.get("127.0.0.1", 11163, "1.3.6.1.2.1.1.2.0");
        System.out.println(oidName);
    }

    @Test
    public void testGetNext(){
        SNMPManager snmpManager = new SNMPManager();
        String nextOIDName = snmpManager.getNext("127.0.0.1", 11163, "1.3.6.1.2.1.1.2.0");
        System.out.println(nextOIDName);
    }

    @Test
    public void testSet() {
        SNMPManager snmpManager = new SNMPManager();
        String ret = snmpManager.set("127.0.0.1", 11163, "1.3.6.1.2.1.1.2.0", "ttttt");
        System.out.println(ret);
    }
}
