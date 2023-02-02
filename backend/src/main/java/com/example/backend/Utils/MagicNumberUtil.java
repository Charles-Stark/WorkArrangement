package com.example.backend.Utils;

public class MagicNumberUtil {
    
    public static String getMagicNumber(byte[] bytes, int magicNumberLength) {

        StringBuilder magicNumber = new StringBuilder();
        for (int i = 0; i < magicNumberLength / 2; i++) {
            magicNumber.append(Integer.toHexString(bytes[i] >> 4 & 0xF));
            magicNumber.append(Integer.toHexString(bytes[i] & 0xF));
        }

        return magicNumber.toString().toUpperCase();
    }

}
