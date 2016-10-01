package com.raylew.network.test;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * Created by Raymond on 2016/10/1.
 */
public class PacketTracer {
    public static void main(String[] args) throws Exception{
        //Find Network Interface
        InetAddress addr = InetAddress.getByName("192.168.1.81");
        PcapNetworkInterface nif = Pcaps.getDevByAddress(addr);
        //Open Pcap Handle
        int snapLen = 65536;
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        int timeout = 10;
        PcapHandle handle = nif.openLive(snapLen, mode, timeout);
        // Capture Packet
        Packet packet = handle.getNextPacketEx();
        handle.close();
        // Get Packet Information
        IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
        Inet4Address srcAddr = ipV4Packet.getHeader().getSrcAddr();
        Inet4Address dstAddr = ipV4Packet.getHeader().getDstAddr();
        System.out.println(srcAddr+":"+dstAddr);
    }
}
