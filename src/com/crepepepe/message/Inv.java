package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.message.datatype.InvVect;
import com.crepepepe.util.ByteParser;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Inv extends Message implements Receivable{

    private int count;
    private ArrayList<InvVect> inventory;

    public Inv (ByteBuffer buffer) {
        super(CommandMap.INV);
        deserialize(buffer);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        count = (int)parser.parseVarInt().value();
        inventory = new ArrayList<>(count);
        for(int i = 0; i < count; i++) inventory.add(parser.parseInvVect());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Inv{");
        sb.append("header=").append(header);
        sb.append(", count=").append(count);
        sb.append(", inventory=");
        for(InvVect vect : inventory) sb.append(vect).append("/");
        sb.append('}');
        return sb.toString();
    }
}
