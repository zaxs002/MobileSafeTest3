package com.wz.mobilesafetest2.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class Md5Utils {
    public static String md5(String src) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(src.getBytes());

            for (byte b : digest) {
                int i = b & 0xff;
                String d = Integer.toHexString(i);
                if (d.length() == 1) {
                    d = "0" + d;
                }
                sb.append(d);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
