package com.crepepepe.message;

import com.crepepepe.constant.CommandMap;
import com.crepepepe.message.datatype.TxIn;
import com.crepepepe.message.datatype.TxOut;
import com.crepepepe.util.ByteParser;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Tx extends Message implements Receivable{

    private int version;
    private byte marker;                // WTx
    private byte flag;                  // WTx
    private int tx_in_count;
    private ArrayList<TxIn> tx_int;
    private int tx_out_count;
    private ArrayList<TxOut> tx_out;
    private String script_witnesses;    // WTX
    private int lock_time;

    public Tx (ByteBuffer buffer) {
       super(CommandMap.TX);
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        header = new Header(buffer);
        ByteParser parser = new ByteParser(header.getContent());
        version = parser.parseInt(true);

    }
}
