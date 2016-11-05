package com.raylew.network.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Raymond on 2016/10/26.
 * send GetRequest,GetNextRequest PDU to get MIB from agent
 */
public class SNMPManager {
    private SNMPConfig snmpConfig;

    public SNMPManager() {
        snmpConfig = new SNMPConfig();
    }

    /**
     * snmpget is an SNMP application that
     * uses the SNMP GET request to query for information on a network entity
     *
     * @param agentIp   ip of agent
     * @param agentPort port of agent
     * @param oid       OID of agent
     * @return
     */
    public String get(String agentIp, int agentPort, String oid) {
        String name = "unknown";
        try {
            Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
            //target agent
            CommunityTarget target = new CommunityTarget();
            //set the security name
            target.setCommunity(new OctetString("public"));
            //set SNMP version
            target.setVersion(SnmpConstants.version2c);
            //set ip address and port
            //127.0.0.1/11163
            String address = String.format("%s/%s", agentIp, agentPort);
            target.setAddress(new UdpAddress(address));
            //set times of retry
            target.setRetries(1);
            //set timeout
            target.setTimeout(5000);
            //listen response from agent
            snmp.listen();
            //new request PDU
            PDU request = new PDU();
            //set pdu type and set oid
            request.setType(PDU.GET);
            //set OID 1.3.6.1.2.1.1.2.0
            request.add(new VariableBinding(new OID(oid)));
            //print request PDU
            System.out.println("request UDP:" + request);
            //send request pdu to target agent
            ResponseEvent responseEvent = snmp.send(request, target);
            //get response PDU
            PDU response = responseEvent.getResponse();
            System.out.println("response UDP:" + response);
            //process response PDU
            if (response != null) {
                if (response.getErrorIndex() == response.noError
                        && response.getErrorStatus() == response.noError) {
                    Vector<? extends VariableBinding> variableBindings = responseEvent.getResponse()
                            .getVariableBindings();
                    Enumeration<? extends VariableBinding> elements = variableBindings.elements();
                    while (elements.hasMoreElements()) {
                        VariableBinding variableBinding = elements.nextElement();
                        name = variableBinding.getVariable().toString();
                    }
                } else {
                    System.out.println("response error:" + response.getErrorStatusText());
                }
            } else {
                System.out.println("response is null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * The snmpset command is used to actually modify information on the remote host.
     * For each variable you want to set, you need to specify the OID to update,
     * the data type and the value you want to set it to.
     *
     * @param agentIp   ip of agent
     * @param agentPort port of agent
     * @param oid       OID of agent
     * @param name      name of agent
     * @return
     */
    public String set(String agentIp, int agentPort, String oid, String name) {
        String ret = "unknown";
        try {
            Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
            //target agent
            CommunityTarget target = new CommunityTarget();
            //set the security name
            target.setCommunity(new OctetString("public"));
            //set SNMP version
            target.setVersion(SnmpConstants.version2c);
            //set ip address and port
            //127.0.0.1/11163
            String address = String.format("%s/%s", agentIp, agentPort);
            target.setAddress(new UdpAddress(address));
            //set times of retry
            target.setRetries(1);
            //set timeout
            target.setTimeout(5000);
            //listen response from agent
            snmp.listen();
            //new request PDU
            PDU request = new PDU();
            //set pdu type and set oid
            request.setType(PDU.SET);
            //set OID 1.3.6.1.2.1.1.2.0
            request.add(new VariableBinding(new OID(oid), new OctetString(name)));
            //print request PDU
            System.out.println("request UDP:" + request);
            //send request PDU to target agent
            ResponseEvent responseEvent = snmp.send(request, target);
            //get response PDU
            PDU response = responseEvent.getResponse();
            System.out.println("response UDP:" + response);
            //process response PDU
            if (response != null) {
                if (response.getErrorIndex() == response.noError
                        && response.getErrorStatus() == response.noError) {
                    Vector<? extends VariableBinding> variableBindings = response.getVariableBindings();
                    Enumeration<? extends VariableBinding> elements = variableBindings.elements();
                    while (elements.hasMoreElements()) {
                        VariableBinding variableBinding = elements.nextElement();
                        ret = variableBinding.getVariable().toString();
                    }
                } else {
                    System.out.println("response error:" + response.getErrorStatusText());
                }
            } else {
                System.out.println("response is null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * The GETNEXT operation retrieves the value of the next OID in the tree
     */
    public String getNext(String agentIp, int agentPort, String currentOID) {
        String nextOID = snmpConfig.getNextOID(currentOID);
        if (nextOID != null&& !"".equals(nextOID)){
            String nextOIDName = this.get(agentIp, agentPort, nextOID);
            return nextOIDName;
        }else{
            return "unknown";
        }
    }
}
