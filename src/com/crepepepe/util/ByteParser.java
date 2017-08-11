package com.crepepepe.util;

import com.crepepepe.message.datatype.IPv6;
import com.crepepepe.message.datatype.InvVect;
import com.crepepepe.message.datatype.NetAddr;
import com.crepepepe.message.datatype.VarInt;
import com.crepepepe.exception.InvalidIpAddressException;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ByteParser {

    private byte[] bytes;
    private int position;

    public ByteParser (byte[] bytes) {
        this.bytes = bytes;
        position = 0;
    }

    public ByteParser (ByteBuffer byteBuffer) {
        this.bytes = byteBuffer.array();
        position = 0;
    }

    public byte[] parseRemain() {
        return ByteUtils.subBytes(bytes, position, bytes.length - position);
    }

    public int parseInt (boolean reverse) {
        byte[] sub = reverse ? ByteUtils.reverseBytes(bytes, position, 4) : ByteUtils.subBytes(bytes, position, 4);
        position += 4;
        return (int)Long.parseLong(ByteUtils.bytesToHexString(sub), 16);
    }

    public long parseLong (boolean reverse) {
        byte[] sub = reverse ? ByteUtils.reverseBytes(bytes, position, 8) : ByteUtils.subBytes(bytes, position, 8);
        position += 8;
        return new BigInteger(ByteUtils.bytesToHexString(sub), 16).longValue();
    }

    public VarInt parseVarInt () {
        VarInt varInt = new VarInt(bytes, position);
        position += varInt.length();
        return varInt;
    }

    public String parseHexString (int length) {
        byte[] sub = ByteUtils.subBytes(bytes, position, length);
        position += length;
        return ByteUtils.bytesToHexString(sub);
    }

    public String parseCharString (int length) {
        byte[] sub = ByteUtils.subBytes(bytes, position, length);
        position += length;
        return ByteUtils.bytesToCharString(sub);
    }

    public byte[] parseByte (int length) {
        byte[] sub = ByteUtils.subBytes(bytes, position, length);
        position += length;
        return sub;
    }

    public int parsePort () {
        byte[] sub = ByteUtils.subBytes(bytes, position, 2);
        position += 2;
        return (int)Long.parseLong(ByteUtils.bytesToHexString(sub) ,16);
    }

    public IPv6 parseIPv6 () {
        StringBuilder sb = new StringBuilder(39);
        byte[] sub = ByteUtils.subBytes(bytes, position, 16);
        position += 16;
        String[] address = Utils.splitByLength(ByteUtils.bytesToHexString(sub), 4);
        for(int i = 0; i < address.length; i++){
            sb.append(address[i]);
            if(i != address.length - 1)
                sb.append(":");
        }

        IPv6 ipv6 = null;

        try {
            ipv6 = new IPv6(sb.toString());
        } catch (InvalidIpAddressException e){
            e.printStackTrace();
        }

        return ipv6;
    }

    public NetAddr parseNetAddr () {
        byte[] sub = ByteUtils.subBytes(bytes, position, 30);
        position += 30;
        return new NetAddr(sub);
    }

    public InvVect parseInvVect () {
        byte[] sub = ByteUtils.subBytes(bytes, position, 36);
        position += 36;
        return new InvVect(sub);
    }
}
