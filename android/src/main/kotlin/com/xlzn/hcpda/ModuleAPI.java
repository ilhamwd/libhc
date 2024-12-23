package com.xlzn.hcpda;

public class ModuleAPI {
    public static int getVersionCode = BuildConfig.API_VERSION;
    private static ModuleAPI moduleAPI = new ModuleAPI();

    public native int CalcCRC(byte[] bArr, int i, byte[] bArr2);

    public native int SerailClose(int i);

    public native int SerailOpen(String str, int i, int i2, int i3, int i4);

    public native int SerailReceive(int i, byte[] bArr, int i2);

    public native int SerailSendData(int i, byte[] bArr, int i2);

    static {
        System.loadLibrary("ModuleAPI");
    }

    private ModuleAPI() {
    }

    public static ModuleAPI getInstance() {
        return moduleAPI;
    }
}
