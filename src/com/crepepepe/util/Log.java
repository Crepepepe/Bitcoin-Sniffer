package com.crepepepe.util;

import com.crepepepe.constant.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss.SSS]");
    private FileOutputStream fos;

    private Log (FileOutputStream fos) {
        this.fos = fos;
    }

    public static void e (String info) {
        if(Constants.DEBUG) {
            System.out.print(info);
            System.out.println(sdf.format(new Date()));
        }
    }

    public static Log get (String path) {
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(path);
        } catch (IOException e) {
            System.out.println("Wrong file path");
            return null;
        }

        return new Log(fos);
    }

    public void log (String text) {
        try {
            fos.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
