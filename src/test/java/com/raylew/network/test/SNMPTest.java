package com.raylew.network.test;

import com.raylew.network.snmp.SNMPData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raymond on 2016/10/26.
 */
public class SNMPTest {
    @Test
    public void testGet() {
        String ip = "192.168.1.70";
        String community = "public";
        // String oidval = "1.3.6.1.2.1.1.6.0";
        String oidval = "1.3.6.1.2.1.1.1.0";

        SNMPData.snmpGet(ip, community, oidval);
    }

    @Test
    public void testGetList() {
        String ip = "127.0.0.1";
        String community = "public";
        List<String> oidList = new ArrayList<String>();
        oidList.add("1.3.6.1.2.1.1.5.0");
        oidList.add("1.3.6.1.2.1.1.7.0");
        SNMPData.snmpGetList(ip, community, oidList);
    }

    @Test
    public void testGetAsyList() {
        String ip = "127.0.0.1";
        String community = "public";
        List<String> oidList = new ArrayList<String>();
        oidList.add("1.3.6.1.2.1");
        oidList.add("1.3.6.1.2.12");
        SNMPData.snmpAsynGetList(ip, community, oidList);
        System.out.println("i am first!");
    }

    @Test
    public void testWalk() {
        String ip = "127.0.0.1";
        String community = "public";
        String targetOid = "1.3.6.1.2.1.1.5.0";
        SNMPData.snmpWalk(ip, community, targetOid);
    }

    @Test
    public void testAsyWalk() {
        String ip = "127.0.0.1";
        String community = "public";
        // 异步采集数据
        SNMPData.snmpAsynWalk(ip, community, "1.3.6.1.2.1.25.4.2.1.2");
    }

    @Test
    public void testSetPDU() throws Exception {
        String ip = "127.0.0.1";
        String community = "public";
        SNMPData.setPDU(ip, community, "1.3.6.1.2.1.1.6.0", "jianghuiwen");
    }

    @Test
    public void testVersion() {
        System.out.println(org.snmp4j.version.VersionInfo.getVersion());
    }

    @Test
    public void testGetGateWay(){
        String ipOfGateway = SNMPData.getIpOfGateway();
        System.out.println(ipOfGateway);
    }
}
