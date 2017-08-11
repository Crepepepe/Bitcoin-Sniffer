package com.crepepepe.message.datatype;

import com.crepepepe.util.ByteParser;

public class TxIn {

    private OutPoint previous_output;
    private int script_length;
    private String signature_script;
    private int sequence;

    public TxIn (byte[] bytes) {
        ByteParser parser = new ByteParser(bytes);
        previous_output = new OutPoint(parser.parseByte(36));
        script_length = (int)parser.parseVarInt().value();
        signature_script = parser.parseHexString(script_length);
        sequence = parser.parseInt(true);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TxIn{");
        sb.append("previous_output=").append(previous_output);
        sb.append(", script_length=").append(script_length);
        sb.append(", signature_script='").append(signature_script).append('\'');
        sb.append(", sequence=").append(sequence);
        sb.append('}');
        return sb.toString();
    }
}
