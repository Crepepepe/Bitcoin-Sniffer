package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.message.datatype.NetAddr;
import com.crepepepe.util.ByteParser;
import com.crepepepe.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Addr extends Message implements Receivable {

    private long count;
    private List<NetAddr> addr_list;

    public Addr (ByteBuffer buffer) {
        super(CommandMap.ADDR);
        deserialize(buffer);
    }

    @Override
    public void deserialize (ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        count = parser.parseVarInt().value();
        addr_list = new ArrayList<>((int)count);
        for(int i = 0; i < count; i++) {
            NetAddr addr = parser.parseNetAddr();
            addr_list.add(addr);
        }
        Log.e(  "[INFO]" + count + " of NetAddr has been processed");
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder("Addr{");
        sb.append("header=").append(header);
        sb.append(", count=").append(count);
        sb.append(", addr_list=");
        for(NetAddr addr : addr_list) sb.append(addr).append("/");
        sb.append('}');
        return sb.toString();
    }

    public Stream<NetAddr> getStream() {
        return addr_list.stream();
    }
}
