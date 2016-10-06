package com.raylew.network.test;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.IpNumber;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by Raymond on 2016/10/1.
 */
public class PacketTracer {
    public static void main(String[] args){
        //Find Network Interface
        InetAddress addr = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if(networkInterfaces!=null){
                while(networkInterfaces.hasMoreElements()){
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    if(inetAddresses!=null){
                        while(inetAddresses.hasMoreElements()){
                            InetAddress inetAddress = inetAddresses.nextElement();
                            if(inetAddress.getHostAddress().startsWith("192")){
                                addr=inetAddress;
                            }
                        }
                    }
                }
            }
            //your local ip
            if(addr==null) {
                addr = InetAddress.getByName("192.168.1.110");
            }
            System.out.println("ip:"+addr.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        PcapNetworkInterface nif = null;
        try {
            nif = Pcaps.getDevByAddress(addr);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
        //Open Pcap Handle
        int snapLen = 65536;
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        int timeout = 10;
        PcapHandle handle = null;
        try {
            handle = nif.openLive(snapLen, mode, timeout);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
        int packetCount=0;
        while(packetCount<20){
            // Capture Packet
            try {
                Packet packet = handle.getNextPacketEx();
                // Get Packet Information
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                Inet4Address srcAddr = ipV4Packet.getHeader().getSrcAddr();
                Inet4Address dstAddr = ipV4Packet.getHeader().getDstAddr();
                IpNumber protocol = ipV4Packet.getHeader().getProtocol();
                System.out.println(packetCount+":source ip:"+srcAddr +
                        ",destination ip:"+ dstAddr+
                        ",protocol:"+protocol.valueAsString());
            }catch (Exception ex){
                System.out.println(packetCount + ":failed" );
            }finally {
                try {
                    //每次抓包间隔1秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            packetCount++;
        }
        handle.close();
    }
}
