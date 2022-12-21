package com.example.backend.Utils;


import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {

    public static String getSHA256(String data) {
        try {
            return DigestUtils.sha256Hex(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isHashSame(String data, String hash) {
        try {
            return hash.equals(getSHA256(data));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
