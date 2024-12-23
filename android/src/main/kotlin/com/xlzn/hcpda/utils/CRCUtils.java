package com.xlzn.hcpda.utils;

public class CRCUtils {
    private static final int BITS_OF_BYTE = 8;
    private static final int INITIAL_VALUE = 65535;
    private static final int POLYNOMIAL = 4129;

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int crc16(byte[] r7) {
        /*
            r0 = 65535(0xffff, float:9.1834E-41)
            int r1 = r7.length
            r2 = 0
        L_0x0005:
            if (r2 >= r1) goto L_0x0024
            byte r3 = r7[r2]
            r4 = r3 & 255(0xff, float:3.57E-43)
            r0 = r0 ^ r4
            r4 = 0
        L_0x000d:
            r5 = 8
            if (r4 >= r5) goto L_0x0021
            r5 = r0 & 1
            r6 = 1
            if (r5 != r6) goto L_0x001b
            int r5 = r0 >> 1
            r5 = r5 ^ 4129(0x1021, float:5.786E-42)
            goto L_0x001d
        L_0x001b:
            int r5 = r0 >> 1
        L_0x001d:
            r0 = r5
            int r4 = r4 + 1
            goto L_0x000d
        L_0x0021:
            int r2 = r2 + 1
            goto L_0x0005
        L_0x0024:
            int r1 = revert(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xlzn.hcpda.utils.CRCUtils.crc16(byte[]):int");
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] crc16Bytes(byte[] r8) {
        /*
            r0 = 65535(0xffff, float:9.1834E-41)
            int r1 = r8.length
            r2 = 0
            r3 = 0
        L_0x0006:
            r4 = 1
            if (r3 >= r1) goto L_0x0025
            byte r5 = r8[r3]
            r6 = r5 & 255(0xff, float:3.57E-43)
            r0 = r0 ^ r6
            r6 = 0
        L_0x000f:
            r7 = 8
            if (r6 >= r7) goto L_0x0022
            r7 = r0 & 1
            if (r7 != r4) goto L_0x001c
            int r7 = r0 >> 1
            r7 = r7 ^ 4129(0x1021, float:5.786E-42)
            goto L_0x001e
        L_0x001c:
            int r7 = r0 >> 1
        L_0x001e:
            r0 = r7
            int r6 = r6 + 1
            goto L_0x000f
        L_0x0022:
            int r3 = r3 + 1
            goto L_0x0006
        L_0x0025:
            int r1 = revert(r0)
            r3 = 2
            byte[] r3 = new byte[r3]
            int r5 = r1 >> 8
            r5 = r5 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5
            r3[r2] = r5
            r2 = r1 & 255(0xff, float:3.57E-43)
            byte r2 = (byte) r2
            r3[r4] = r2
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xlzn.hcpda.utils.CRCUtils.crc16Bytes(byte[]):byte[]");
    }

    private static int revert(int src) {
        return ((65280 & src) >> 8) | ((src & 255) << 8);
    }
}
