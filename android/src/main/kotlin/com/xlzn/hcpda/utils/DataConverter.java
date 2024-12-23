package com.xlzn.hcpda.utils;

import android.util.Log;

import java.util.Arrays;

import kotlin.UByte;

public class DataConverter {
    public static String bytesToHex(byte[] b) {
        if (b == null) return null;

        StringBuilder hexString = new StringBuilder();
        for (byte bb : b) {
            // Convert to unsigned int and format as a 2-character hex string
            hexString.append(String.format("%02x", bb & 0xFF));
        }
        return hexString.toString();
    }

    public static byte[] hexToBytes(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        byte[] bytes = new byte[(s.length() / 2)];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(i * 2, (i * 2) + 2), 16);
        }
        Log.d("QWERTYUIOP", Arrays.toString(bytes));
        return bytes;
    }
}
