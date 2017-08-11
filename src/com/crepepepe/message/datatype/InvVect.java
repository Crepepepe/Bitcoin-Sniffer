package com.crepepepe.message.datatype;

import com.crepepepe.constant.Constants;
import com.crepepepe.constant.InvType;
import com.crepepepe.util.ByteParser;
import com.crepepepe.util.ByteUtils;

public class InvVect {

    private InvType type;
    private String hash;

    public InvVect (byte[] bytes) {
        ByteParser parser = new ByteParser(bytes);

        int type = parser.parseInt(true);
        if(type <= InvType.MSG_CMPCT_BLOCK.value) {
            this.type = Constants.INV_TYPES[type];
        } else {
            if(type == InvType.MSG_WITNESS_BLOCK.value)
                this.type = InvType.MSG_WITNESS_BLOCK;
            else if(type == InvType.MSG_WITNESS_TX.value)
                this.type = InvType.MSG_WITNESS_TX;
            else if(type == InvType.MSG_FILTERED_WITNESS_BLOCK.value)
                this.type = InvType.MSG_FILTERED_WITNESS_BLOCK;
        }

        hash = ByteUtils.bytesToHexString(parser.parseByte(32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvVect{");
        sb.append("type=").append(type);
        sb.append(", hash='").append(hash).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
