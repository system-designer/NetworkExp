package com.raylew.network.snmp;

import org.snmp4j.*;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by Raymond on 2016/10/26.
 * listen manager request,send response to manager
 */
public class SNMPAgent {
    public static class Handler implements CommandResponder {
        protected String agentAddress;
        protected int agentPort = 0;
        protected String agentCommunityName;
        protected TransportMapping agentServerSocket;
        protected Snmp agentSNMP;
        protected SNMPConfig snmpConfig;

        public Handler() {
        }

        public void configure() {
            agentAddress = "127.0.0.1";
            agentPort = 11163;
            agentCommunityName = "public";
            snmpConfig = new SNMPConfig();
        }

        /**
         * start listen
         */
        public void start() {
            try {
                //ip:port set up UDP socket
                agentServerSocket = new DefaultUdpTransportMapping(
                        new UdpAddress(InetAddress.getByName(agentAddress), agentPort));
                agentSNMP = new Snmp(agentServerSocket);
                agentSNMP.addCommandResponder(this);
                agentServerSocket.listen();
            } catch (UnknownHostException vException) {
                System.out.println("start:" + vException);
            } catch (IOException vException) {
                System.out.println(vException);
            }
        }

        /**
         * when accept pdu from manager,process it
         *
         * @param responderEvent
         */
        @Override
        public synchronized void processPdu(CommandResponderEvent responderEvent) {
            String agentOid = "";
            String agentName = "";
            String vCommunityName = new String(responderEvent.getSecurityName());
            System.out.println("community:" + vCommunityName);
            //pdu content structure:hashMap
            PDU pdu = responderEvent.getPDU();
            SNMPConfig config = new SNMPConfig();
            if (pdu == null) {
                System.out.println("pdu is null");
            } else {
                //print pdu from manager
                System.out.println("manager request pdu:" + pdu.toString());
                switch (pdu.getType()) {
                    case PDU.GET:
                        //get name of oid from agent MIB
                        Vector<? extends VariableBinding> variableBindings = pdu.getVariableBindings();
                        Enumeration<? extends VariableBinding> elements = variableBindings.elements();
                        while (elements.hasMoreElements()) {
                            VariableBinding variableBinding = elements.nextElement();
                            agentOid = variableBinding.getOid().toString();
                            agentName = snmpConfig.getValueByOID(agentOid);
                            System.out.println(agentOid + ":" + agentName);
                        }
                        break;
                    case PDU.GETNEXT:
                        break;
                    case PDU.SET:
                        //set name of agent,update MIB
                        Vector<? extends VariableBinding> variableBindings3 = pdu.getVariableBindings();
                        Enumeration<? extends VariableBinding> elements3 = variableBindings3.elements();
                        while (elements3.hasMoreElements()) {
                            VariableBinding variableBinding = elements3.nextElement();
                            agentOid = variableBinding.getOid().toString();
                            String oidName = variableBinding.getVariable().toString();
                            config.setValueByOID(agentOid, oidName);
                            System.out.println(agentOid + ":" + oidName);
                        }
                        break;
                    case PDU.TRAP:
                        break;
                }
                //generate response to manager
                StatusInformation statusInformation = new StatusInformation();
                StateReference ref = responderEvent.getStateReference();
                try {
                    switch (pdu.getType()) {
                        case PDU.GET:
                            //return name of oid
                            pdu.set(0, new VariableBinding(new OID(agentOid),
                                    new OctetString(agentName)));
                            break;
                        case PDU.GETNEXT:
                            break;
                        case PDU.SET:
                            //return set result
                            pdu.set(0, new VariableBinding(new OID(agentOid),
                                    new OctetString("ok")));
                            break;
                        case PDU.TRAP:
                            break;
                    }
                    pdu.setType(PDU.RESPONSE);
                    responderEvent.getMessageDispatcher().returnResponsePdu(
                            responderEvent.getMessageProcessingModel(),
                            responderEvent.getSecurityModel(),
                            responderEvent.getSecurityName(),
                            responderEvent.getSecurityLevel(), pdu,
                            responderEvent.getMaxSizeResponsePDU(), ref,
                            statusInformation);
                } catch (MessageException vException) {
                    System.out.println(vException);
                }
            }
        }
    }

    public static void main(String[] args) {
        Handler h = new Handler();
        //initialize parameters
        h.configure();
        h.start();
        while (true) {
            System.out.println("----------listen-------------");
            synchronized (SNMPAgent.class) {
                try {
                    SNMPAgent.class.wait();
                } catch (Exception e) {
                }
            }
        }
    }
}
