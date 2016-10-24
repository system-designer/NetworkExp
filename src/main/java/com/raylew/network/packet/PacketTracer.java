package com.raylew.network.packet;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.IpNumber;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Raymond on 2016/10/10.
 */
public class PacketTracer {
    public static void main(String[] args){
        //Find Network Interface
        InetAddress addr = null;
        String localIp=null;
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
            //default local ip
            if(addr==null) {
                addr = InetAddress.getByName("192.168.1.110");
            }
            localIp=addr.getHostAddress();
            System.out.println("local ip:"+addr.getHostAddress());
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
        //gather packet
        List<PacketStat> packetStats=new ArrayList<PacketStat>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        //2 minutes
        long timeMillis = System.currentTimeMillis();
        timeMillis+=2*60*1000;
        int packetCount=0;
        while(System.currentTimeMillis()<timeMillis){
            // Capture Packet
            try {
                Packet packet = handle.getNextPacketEx();
                // Get Packet Information
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                Inet4Address srcAddr = ipV4Packet.getHeader().getSrcAddr();
                Inet4Address dstAddr = ipV4Packet.getHeader().getDstAddr();
                IpNumber ipNumber = ipV4Packet.getHeader().getProtocol();

                PacketStat packetStat=new PacketStat();
                packetStat.setIpNumber((int)ipNumber.value());
                String protocol=ipNumber.toString();
                protocol=protocol.substring(protocol.indexOf("(")+1,protocol.indexOf(")"));
                packetStat.setProtocol(protocol);
                packetStat.setSource(srcAddr.getHostAddress());
                packetStat.setDestination(dstAddr.getHostAddress());
                packetStat.setTimestamp(simpleDateFormat.format(new Date()));
                packetStats.add(packetStat);
                /*
                System.out.println(packetStat.toString());
                */
            }catch (Exception ex){
                /*
                System.out.println(packetCount + ":failed" );
                */
            }finally {
                /*
                try {
                    //每次抓包间隔1秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
            }
            packetCount++;
        }
        handle.close();
        //stat packet
        Map<String,Integer> sourceMap=new HashMap<String,Integer>();
        Map<String,Integer> destinationMap=new HashMap<String,Integer>();
        int size=packetStats.size();
        for(int index=0;index<size;index++) {
            PacketStat packetStat = packetStats.get(index);
            String key=packetStat.getProtocol();
            //source ip
            if(packetStat.getSource().equals(localIp)){
                if(sourceMap.containsKey(key)){
                    int value=sourceMap.get(key);
                    sourceMap.put(key,++value);
                }else{
                    sourceMap.put(key,1);
                }
            }
            //destination ip
            if(packetStat.getDestination().equals(localIp)){
                if(destinationMap.containsKey(key)){
                    int value=destinationMap.get(key);
                    destinationMap.put(key,++value);
                }else{
                    destinationMap.put(key,1);
                }
            }
        }
        //output stat
        System.out.println(localIp+"为源ip:");
        Iterator<String> iterator = sourceMap.keySet().iterator();
        while (iterator.hasNext()){
            String key=iterator.next();
            System.out.println(key+":"+sourceMap.get(key));
        }
        System.out.println(localIp+"为目的ip:");
        iterator = destinationMap.keySet().iterator();
        while (iterator.hasNext()){
            String key=iterator.next();
            System.out.println(key+":"+destinationMap.get(key));
        }
    }
}
