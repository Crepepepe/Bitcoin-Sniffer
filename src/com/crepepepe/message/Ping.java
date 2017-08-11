package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.util.ByteParser;

import java.nio.ByteBuffer;

public class Ping extends Message implements Receivable {

    private long nonce;

    public Ping (ByteBuffer buffer) {
        super(CommandMap.PING);
        deserialize(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        if(header.getPayloadSize() == 0) {
            nonce = 0;
            return;
        }

        ByteParser parser = new ByteParser(header.getContent());
        nonce = parser.parseLong(true);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ping{");
        sb.append("nonce=").append(String.format("0x%x",nonce));
        sb.append('}');
        return sb.toString();
    }

    public long getNonce() {
        return nonce;
    }
}
