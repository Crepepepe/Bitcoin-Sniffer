package com.crepepepe.connector;

import com.crepepepe.constant.Constants;
import com.crepepepe.exception.InvalidIpAddressException;
import com.crepepepe.message.*;
import com.crepepepe.message.datatype.NetAddr;
import com.crepepepe.message.datatype.OutPoint;
import com.crepepepe.util.ByteUtils;
import com.crepepepe.util.Log;
import com.crepepepe.util.MessageConcatter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeConnector extends Thread {

    private String ip;
    private int port;
    private AsynchronousSocketChannel socket;
    private AsynchronousChannelGroup group;
    private HashSet<MessageConcatter> sets;
    private Version recvVersion;
    private boolean isConnected;

    public NodeConnector(String ip, int port) {
        this.ip = ip;
        this.port = port;
        sets = new HashSet<>();
    }

    @Override
    public void run() {
        try {
            group = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
            socket = AsynchronousSocketChannel.open(group);
            socket.connect(new InetSocketAddress(ip, port), null, new CompletionHandler<Void, Void>() {

                @Override
                public void completed(Void result, Void attachment) {
                    try {
                        Log.e("[INFO]Connected to " + ip + ":" + port);
                        isConnected = true;
                        sendMessage(new Version(ip, port));
                        recvMessage();
                    } catch (InvalidIpAddressException e) {
                        close("[INFO]Invalid ip address");
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    isConnected = false;
                    exc.printStackTrace();
                    close("Fail to connect to " + ip + ":" + port);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Sendable message) {
        try {
            socket.write(message.serialize(), null, new CompletionHandler<Integer, Void>() {

                @Override
                public void completed(Integer result, Void attachment) {
                    Log.e("[->]Send a " + message.getMessageName() + " message [" + result + " byte]");
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    Log.e("[->]Fail to send a " + message.getMessageName() + " message");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Attachment may be split. Must consider concatenate.
     * Case 1. Receive non split message
     * Header of this case will be valid.
     * Case 2. Receive header of split message; Case 2 and Case 3 mush have payload. (Version, Addr, Inv, Tx, Ping, ...)
     * Checksum will be not matched.
     * Use MessageConcatter class to save buffer of header.
     * Case 3. Receive payload of split message
     * Can't cast buffer to Header class.
     * Concat header and payload by MessageConcatter.
     */
    private void recvMessage() {
        ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);

        socket.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                Log.e("[<-]Receive " + result + " byte");
                if (result != -1) {
                    byte[] start = new byte[4];
                    attachment.rewind();
                    attachment.get(start, 0, 4);
                    Header header = null;
                    boolean isValidStartString = false;
                    boolean isValidChecksum = false;
                    if ((int) Long.parseLong(ByteUtils.bytesToHexString(start), 16) == Constants.START_STRING) {
                        header = new Header(attachment);
                        isValidStartString = true;
                        isValidChecksum = header.isValidChecksum();
                        Log.e(header.toString());
                    }

                    if (isValidStartString && isValidChecksum) { // Case 1
                        Log.e("[Case1]");
                        switch (header.getCommandName()) {
                            case "version":
                                recvVersion = new Version(attachment);
                                Log.e(recvVersion.toString());
                                sendMessage(new Verack());
                                break;
                            case "verack":
                                break;
                            case "ping":
                                Ping ping = new Ping(attachment);
                                Log.e(ping.toString());
                                sendMessage(new Pong(ping.getNonce()));
                                sendMessage(new GetAddr());
                                break;
                            case "addr":
                                Addr addr = new Addr(attachment);
                                break;
                            case "inv":
                                Inv inv = new Inv(attachment);
                                Log.e(inv.toString());
                                break;
                            case "tx":
                                break;
                            case "alert":
                                Alert alert = new Alert(attachment);
                                Log.e(alert.toString());
                                break;
                            case "utxos":
                                Utxos utxos = new Utxos(attachment);
                                Log.e(utxos.toString());
                            default:
                                Log.e("[INFO]Unhandled message: " + header.getCommandName());
                                break;
                        }
                    } else {
                        if (isValidStartString) { // Case 2
                            sets.add(new MessageConcatter(attachment, header.getRecvChecksum(), header.getCommandName(), header.getPayloadSize(), result));
                        } else { // Case 3
                            Log.e("[Case3]");
                            MessageConcatter concatter = sets.iterator().next();
                            if (concatter != null) {
                                concatter.addContent(attachment, result);
                                Log.e(concatter.toString());
                                if (concatter.getFilled() >= concatter.getPayload()) {
                                    Message message = concatter.concat();
                                    switch (concatter.getCommandName()) {
                                        case "version":
                                            recvVersion = (Version) message;
                                            Log.e(recvVersion.toString());
                                            sendMessage(new Verack());
                                            break;
                                        case "ping":
                                            Ping ping = (Ping) message;
                                            Log.e(ping.toString());
                                            sendMessage(new Pong(ping.getNonce()));
                                            sendMessage(new GetAddr());
                                            break;
                                        case "addr":
                                            Addr addr = (Addr) message;
                                            messageProcess(addr);
                                            close("[INFO]getAddr Done");
                                            break;
                                        case "inv":
                                            Inv inv = (Inv) message;
                                            Log.e(inv.toString());
                                            break;
                                        case "tx":
                                            break;
                                        case "alert":
                                            Alert alert = (Alert) message;
                                            Log.e(alert.toString());
                                            break;
                                        case "utxos":
                                            Utxos utxos = (Utxos) message;
                                            Log.e(utxos.toString());
                                            break;
                                        default:
                                            Log.e("[INFO]Unhandled message: " + concatter.getCommandName());
                                            break;
                                    }
                                    sets.remove(concatter);
                                }
                            } else {
                                close("[ERROR]Wrong bytes has been received");
                                return;
                            }
                        }
                    }
                }

                if (result == -1) {
                    close("[INFO]Connection closed");
                    return;
                }

                if (group != null && !group.isShutdown()) {
                    ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
                    socket.read(buffer, buffer, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                close("[INFO]Fail to receive");
            }
        });
    }

    private void test () {
        ArrayList<OutPoint> list = new ArrayList<>(1);
        OutPoint outPoint1 = new OutPoint("b27d535ac226966bed75f805062f499f99fb50a1", 0);
        OutPoint outPoint2 = new OutPoint("56a05362d8a7296c322cd012f0252934673bc5d4", 1);
        list.add(outPoint1);
        list.add(outPoint2);
        GetTutxos getTutxos = new GetTutxos(false, list);
        sendMessage(getTutxos);
    }

    private void messageProcess (Message message) {
        if(message instanceof Addr) {
            Stream<NetAddr> stream = ((Addr) message).getStream();
            List<NetAddr> netAddr = stream.sorted(Comparator.comparing(NetAddr::getTime)).collect(Collectors.toList());
            String now = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            File file = new File(now + ".txt");
            try {
                if(file.createNewFile()) {
                    FileOutputStream fos = new FileOutputStream(file);
                    for (NetAddr net : netAddr) {
                        fos.write(String.format("%d %d %s %d\n", net.getTime(), net.getServices(), net.getIp(), net.getPort()).getBytes());
                    }
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close(String info) {
        if (group != null && !group.isShutdown()) {
            Log.e(info);
            try {
                isConnected = false;
                socket.close();
                group.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}
