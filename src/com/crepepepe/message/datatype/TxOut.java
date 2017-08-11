package com.crepepepe.message.datatype;

import com.crepepepe.util.ByteParser;

public class TxOut {

    private long value;
    private int pk_script_length;
    private String pk_script;

    public TxOut (byte[] bytes) {
        ByteParser parser = new ByteParser(bytes);
        value = parser.parseLong(true);
        pk_script_length = (int)parser.parseVarInt().value();
        pk_script = parser.parseHexString(pk_script_length);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TxOut{");
        sb.append("value=").append(value);
        sb.append(", pk_script_length=").append(pk_script_length);
        sb.append(", pk_script='").append(pk_script).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
