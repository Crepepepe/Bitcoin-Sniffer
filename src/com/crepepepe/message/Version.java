package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.constant.Constants;
import com.crepepepe.message.datatype.IPv6;
import com.crepepepe.message.datatype.Service;
import com.crepepepe.exception.InvalidIpAddressException;
import com.crepepepe.util.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Version extends Message implements Sendable, Receivable {

    private int version;
    private long services;
    private long timestamp;
    private long recvServices;
    private IPv6 recvIp;
    private int recvPort;
    private long transServices;
    private IPv6 transIp;
    private int transPort;
    private long nonce;
    private int user_agents_bytes;
    private byte[] user_agent;
    private int start_height;
    private int relay;

    private Version() throws InvalidIpAddressException {
        super(CommandMap.VERSION);
        version = Constants.PROTOCOL_VERSION;
        services = Constants.NODE_TYPE;
        timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        recvServices = 0x01;
        transServices = Constants.NODE_TYPE;
        transIp = new IPv6("0.0.0.0");
        transPort = 0;
        nonce = new SecureRandom().nextLong();
        user_agents_bytes = Constants.USER_AGENT_BYTE;
        user_agent = Constants.USER_AGENT;
        start_height = -1;
        if(version > 70001)
            relay = 0;
        else
            relay = -1;
    }

    public Version (String ip, int port) throws InvalidIpAddressException {
        this(new IPv6(ip), port);
    }

    public Version (IPv6 ipv6, int port) throws InvalidIpAddressException {
        this();
        recvIp = ipv6;
        recvPort = port;
    }

    public Version (ByteBuffer buffer) {
        super(CommandMap.VERSION);
        deserialize(buffer);
    }

    @Override
    public ByteBuffer serialize() {
        int size = Constants.DEFAULT_SIZE_OF_VERSION
                + (relay != -1 ? 1 : 0)
                + user_agents_bytes;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        Constants.NONCE = nonce;
        buffer.order(ByteOrder.LITTLE_ENDIAN)
                .putInt(version)
                .putLong(services)
                .putLong(timestamp)
                .putLong(recvServices)
                .order(ByteOrder.BIG_ENDIAN)
                .put(recvIp.toByteArray())
                .putChar((char) recvPort)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(transServices)
                .order(ByteOrder.BIG_ENDIAN)
                .put(transIp.toByteArray())
                .putChar((char) transPort)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(nonce)
                .put((byte) user_agents_bytes);
        if(user_agents_bytes != 0)
            buffer.order(ByteOrder.BIG_ENDIAN).put(user_agent).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(start_height);
        if(relay != -1)
            buffer.put(relay >= 1 ? (byte)1 : (byte)0);
        return insertHeader(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        version = parser.parseInt(true);
        services = parser.parseLong(true);
        timestamp = parser.parseLong(true);
        recvServices = parser.parseLong(true);
        recvIp = parser.parseIPv6();
        recvPort = parser.parsePort();
        transServices = parser.parseLong(true);
        transIp = parser.parseIPv6();
        transPort = parser.parsePort();
        nonce = parser.parseLong(true);
        user_agents_bytes = (int)parser.parseVarInt().value();
        if(user_agents_bytes != 0)
            user_agent = parser.parseByte(user_agents_bytes);
        start_height = parser.parseInt(true);
        relay = version > 70001 ? parser.parseByte(1)[0] & 0xff : -1;
    }

    @Override
    public CommandMap getMessageName() {
        return command;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Version{");
        sb.append("header=").append(header);
        sb.append(", version=").append(version);
        sb.append(", services=").append(new Service(services));
        sb.append(", timestamp=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp * 1000)));
        sb.append(", recvServices=").append(new Service(recvServices));
        sb.append(", recvIp=").append(recvIp);
        sb.append(", recvPort=").append(recvPort);
        sb.append(", transServices=").append(new Service(transServices));
        sb.append(", transIp=").append(transIp);
        sb.append(", transPort=").append(transPort);
        sb.append(", nonce=").append(String.format("0x%x",nonce));
        sb.append(", user_agents_bytes=").append(user_agents_bytes);
        if(user_agents_bytes != 0)
            sb.append(", user_agent=").append(ByteUtils.bytesToCharString(user_agent));
        sb.append(", start_height=").append(start_height);
        sb.append(", relay=").append(relay);
        sb.append('}');
        return sb.toString();
    }
}
