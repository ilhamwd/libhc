package com.xlzn.hcpda.utils;

import android.util.Log;

public class Test {
    public static String AddZero(String str, int length) {
        Log.e("TAG", "AddZero-----------:str  " + str);
        Log.e("TAG", "AddZero-----------:length  " + length);
        if (str == null || str.length() >= length) {
            return str;
        }
        String addzreo = "0";
        for (int i = 0; i < (length - str.length()) - 1; i++) {
            addzreo = addzreo + "0";
        }
        return addzreo + str;
    }

    public static String HexString2binaryString(String hexString) {
        if (hexString.length() != 24) {
            return null;
        }
        int hexString1int = Integer.valueOf(hexString.substring(0, 6), 16).intValue();
        int hexString2int = Integer.valueOf(hexString.substring(6, 12), 16).intValue();
        int hexString3int = Integer.valueOf(hexString.substring(12, 18), 16).intValue();
        int hexString4int = Integer.valueOf(hexString.substring(18, 24), 16).intValue();
        String bin1 = AddZero(Integer.toBinaryString(hexString1int), 24);
        String bin2 = AddZero(Integer.toBinaryString(hexString2int), 24);
        String bin3 = AddZero(Integer.toBinaryString(hexString3int), 24);
        return bin1 + bin2 + bin3 + AddZero(Integer.toBinaryString(hexString4int), 24);
    }

    public static String BinaryToDec(String Binarys) {
        long sum = 0;
        int i = Binarys.length();
        while (true) {
            i--;
            if (i < 0) {
                return sum + "";
            }
            sum = (long) (((double) sum) + (((double) Integer.parseInt(Binarys.charAt(i) + "")) * Math.pow(2.0d, (double) ((Binarys.length() - i) - 1))));
        }
    }

    public static String CheckBit(String str) {
        int js_sum = 0;
        int os_sum = 0;
        int parity_bit = 0;
        int k = 1;
        if (str.length() == 11) {
            for (int i = 0; i < str.length(); i++) {
                if (k < 12) {
                    if (k % 2 != 0 || k == 12) {
                        js_sum += Integer.parseInt(str.charAt(i) + "");
                    } else {
                        os_sum += Integer.parseInt(str.charAt(i) + "");
                    }
                    k++;
                }
            }
            parity_bit = 10 - (((js_sum * 3) + os_sum) % 10);
        }
        return parity_bit + "";
    }

    public static String getUPC(String EPC) {
        String UPCPro;
        String EPCBits = AddZero(HexString2binaryString(EPC), 96);
        String str = Integer.parseInt((String) EPCBits.subSequence(0, 8), 2) + "";
        String str2 = Integer.parseInt((String) EPCBits.subSequence(8, 11), 2) + "";
        String Partition = Integer.parseInt((String) EPCBits.subSequence(11, 14), 2) + "";
        int Digits = 0;
        int CutoffPoint = 0;
        if (Partition == "0") {
            CutoffPoint = 40;
            Digits = 12;
        } else if (Partition.equals("1")) {
            CutoffPoint = 37;
            Digits = 11;
        } else if (Partition.equals("2")) {
            CutoffPoint = 34;
            Digits = 10;
        } else if (Partition.equals("3")) {
            CutoffPoint = 30;
            Digits = 9;
        } else if (Partition.equals("4")) {
            CutoffPoint = 27;
            Digits = 8;
        } else if (Partition.equals("5")) {
            CutoffPoint = 24;
            Digits = 7;
        } else if (Partition.equals("6")) {
            CutoffPoint = 20;
            Digits = 6;
        }
        String GS1CompanyPrefix = AddZero(BinaryToDec(EPCBits.substring(14, CutoffPoint + 14)), Digits);
        String IdIrn = AddZero(BinaryToDec(EPCBits.substring(CutoffPoint + 14, 58)), 12 - Digits);
        if (GS1CompanyPrefix.length() + IdIrn.length() == 13) {
            UPCPro = AddZero(IdIrn.substring(0, 1) + GS1CompanyPrefix + IdIrn.substring(1), 13);
        } else {
            UPCPro = AddZero(GS1CompanyPrefix + IdIrn, 13);
        }
        return UPCPro + CheckBit(UPCPro.substring(2));
    }
}
