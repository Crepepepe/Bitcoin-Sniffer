package com.crepepepe.main;

import com.crepepepe.connector.NodeConnector;
import com.crepepepe.constant.Constants;
import com.crepepepe.util.Lookup;

import java.util.Arrays;

public class Test {
    public static void main (String... args) {
        /*
        String ip;
        if(args.length == 0)
            ip = "2804:07f7:0280:53fc:492e:22a5:c0bc:6d5e";
        else
            ip = args[0];
        Constants.init();
        NodeConnector connector = new NodeConnector(ip, 8333);
        try {
            connector.start();
        } catch (Exception e){
            connector.close("[EXCEPTION]");
            e.printStackTrace();
        }
        */
        Arrays.stream(Lookup.lookup("dnsseed.bitcoin.dashjr.org")).forEach(s -> System.out.println(s.getHostAddress()));
    }
}
