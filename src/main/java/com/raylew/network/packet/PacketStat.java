package com.raylew.network.packet;

/**
 * Created by Raymond on 2016/10/10.
 */
public class PacketStat {
    //协议号
    private Integer IpNumber;
    //协议名称
    private String protocol;
    //源ip
    private String source;
    //目的ip
    private String destination;
    //数据包捕获时间
    private String timestamp;

    public Integer getIpNumber() {
        return IpNumber;
    }

    public void setIpNumber(Integer ipNumber) {
        IpNumber = ipNumber;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PacketStat{" +
                "protocol='" + protocol + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
