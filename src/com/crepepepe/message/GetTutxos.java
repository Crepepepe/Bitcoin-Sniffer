package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.message.datatype.OutPoint;

import java.nio.ByteBuffer;
import java.util.List;

public class GetTutxos extends Message implements Sendable{

    private boolean check_mempool;
    private List<OutPoint> outpoints;

    public GetTutxos (boolean check_mempool, List<OutPoint> outpoints) {
        super(CommandMap.GETUTXOS);
        this.check_mempool = check_mempool;
        this.outpoints = outpoints;
    }

    @Override
    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + outpoints.size() * 36);
        buffer.put(check_mempool ? (byte) 1 : (byte) 0);
        for(OutPoint outPoint : outpoints) {
            buffer.put(outPoint.serialize());
        }

        return insertHeader(buffer);
    }

    @Override
    public CommandMap getMessageName() {
        return command;
    }
}
