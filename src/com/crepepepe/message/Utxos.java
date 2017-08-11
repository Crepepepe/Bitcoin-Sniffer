package com.crepepepe.message;


import com.crepepepe.constant.CommandMap;
import com.crepepepe.util.ByteParser;
import com.crepepepe.util.ByteUtils;

import java.nio.ByteBuffer;

public class Utxos extends Message implements Receivable{

    private int chain_height;
    private String chain_tip_hash;
    private String results;

    public Utxos (ByteBuffer buffer) {
        super(CommandMap.UTXOS);
        deserialize(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        chain_height = parser.parseInt(true);
        chain_tip_hash = parser.parseHexString(32);
        results = ByteUtils.bytesToHexString(parser.parseRemain());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Utxos{");
        sb.append("chain_height=").append(chain_height);
        sb.append(", chain_tip_hash='").append(chain_tip_hash).append('\'');
        sb.append(", results='").append(results).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
