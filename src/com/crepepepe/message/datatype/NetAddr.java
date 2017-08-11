package com.crepepepe.message.datatype;

import com.crepepepe.util.ByteParser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NetAddr {
    private long time;
    private long services;
    private IPv6 ip;
    private int port;

    public NetAddr(byte[] bytes) {
        ByteParser parser = new ByteParser(bytes);
        time = parser.parseInt(true);
        services = parser.parseLong(true);
        ip = parser.parseIPv6();
        port = parser.parsePort();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NetAddr{");
        sb.append("time=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000)));
        sb.append(", services=").append(new Service(services));
        sb.append(", ip=").append(ip);
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }

    public long getTime() {
        return time;
    }

    public long getServices() {
        return services;
    }

    public IPv6 getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
