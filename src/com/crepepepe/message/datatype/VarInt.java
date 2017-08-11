package com.crepepepe.message.datatype;

import com.crepepepe.util.ByteUtils;

public class VarInt {
    private final long value;
    private final int length;
    private final int[] pow = {2, 4, 8};

    public VarInt (byte[] bytes, int offset) {
        int follow = (bytes[0] & 0xff) - 0xfd;
        length = 1 + (follow >= 0 ? pow[follow] : 0);

        if(length > 1)
            value = Long.parseLong(ByteUtils.bytesToHexString(ByteUtils.reverseBytes(bytes, offset + 1, pow[follow])) , 16);
        else
            value = Long.parseLong(ByteUtils.bytesToHexString(ByteUtils.reverseBytes(bytes, offset, length)) , 16);
    }

    public long value() {
        return value;
    }

    public int length() {
        return length;
    }
}
