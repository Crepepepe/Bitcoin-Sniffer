package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Pong extends Message implements Sendable{

    private final long nonce;

    public Pong(long nonce) {
        super(CommandMap.PONG);
        this.nonce = nonce;
    }

    @Override
    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(nonce);
        return insertHeader(buffer);
    }

    @Override
    public CommandMap getMessageName() {
        return command;
    }

    public long getNonce() {
        return nonce;
    }
}
