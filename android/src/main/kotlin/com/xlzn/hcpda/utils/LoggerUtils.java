package com.xlzn.hcpda.utils;

import android.util.Log;

public class LoggerUtils {
    private static String _TAG = "module";
    private static boolean debugFlag = true;

    public static boolean isDebug() {
        return debugFlag;
    }

    public static void d(String TAG, byte[] data) {
        StringBuilder stringBuffer = new StringBuilder();
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String className = stackTrace[3].getFileName();
            String methodName = stackTrace[3].getMethodName();
            int lineNumber = stackTrace[3].getLineNumber();
            stringBuffer.append("[---(").append(className).append(":").append(lineNumber).append(")#").append(methodName.substring(0, 1).toUpperCase() + methodName.substring(1)).append("---] ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(_TAG, " ========> " + (stringBuffer.toString() + ": " + DataConverter.bytesToHex(data)));
    }

    public static void d(String TAG, String msg) {
        if (isDebug()) {
            StringBuilder stringBuffer = new StringBuilder();
            try {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String className = stackTrace[3].getFileName();
                String methodName = stackTrace[3].getMethodName();
                int lineNumber = stackTrace[3].getLineNumber();
                stringBuffer.append("[---(").append(className).append(":").append(lineNumber).append(")#").append(methodName.substring(0, 1).toUpperCase() + methodName.substring(1)).append("---] ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(_TAG, " ========> " + (stringBuffer.toString() + ": " + msg));
        }
    }
}
