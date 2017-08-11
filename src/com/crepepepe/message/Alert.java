package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;

import java.nio.ByteBuffer;

public class Alert extends Message implements Receivable {

    private String readable;

    public Alert (ByteBuffer buffer) {
        super(CommandMap.ALERT);
        deserialize(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        StringBuilder sb = new StringBuilder();
        for(byte b : header.getContent()) {
            int code = (b & 0xff);
            if ((code >= 'A' && code <= 'Z') || (code >= 'a' && code <= 'z') || code == ' ')
                sb.append(Character.toChars(code));
        }

        readable = sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Alert{");
        sb.append("readable='").append(readable).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
