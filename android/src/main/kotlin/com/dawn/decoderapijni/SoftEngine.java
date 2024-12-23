package com.dawn.decoderapijni;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

public class SoftEngine {
    public static final int ENG_IOCTRL_SETUP_BRIGHTNESS = 717;
    public static final int ENG_IOCTRL_SETUP_GAIN = 716;
    public static final int ENG_IOCTRL_SETUP_LIGHTTIME = 718;
    private static final int JNI_IOCTRL_GET_DECODE_VERSION = 726;
    private static final int JNI_IOCTRL_GET_FOCUS_DECODE_ENABLE = 767;
    public static final int JNI_IOCTRL_GET_HIGHLIGHT_FRAMES = 714;
    public static final int JNI_IOCTRL_GET_HIGHLIGHT_SUPPORT = 710;
    private static final int JNI_IOCTRL_GET_SCANNER_TEMP = 727;
    private static final int JNI_IOCTRL_GET_SCANNER_VERSION = 724;
    public static final int JNI_IOCTRL_GET_SCAN_ILLUMINATION_ON_OFF = 711;
    private static final int JNI_IOCTRL_GET_SDK_VERSION = 725;
    public static final int JNI_IOCTRL_RESET_ALL_CODE_SETTINGS = 763;
    private static final int JNI_IOCTRL_SETTING_SYSTEM_LANGUAGE = 1010;
    public static final int JNI_IOCTRL_SETUP_EXPOSURE = 715;
    public static final int JNI_IOCTRL_SET_CONTEXT = 754;
    public static final int JNI_IOCTRL_SET_DECODE_IMG = 751;
    private static final int JNI_IOCTRL_SET_FOCUS_DECODE_CALIBRATION = 765;
    private static final int JNI_IOCTRL_SET_FOCUS_DECODE_ENABLE = 764;
    public static final int JNI_IOCTRL_SET_HIGHLIGHT_FRAMES = 713;
    public static final int JNI_IOCTRL_SET_HIGHLIGHT_SUPPORT = 712;
    public static final int JNI_IOCTRL_SET_MULTICODE_SEPARATOR = 704;
    private static final int JNI_IOCTRL_SET_NLSCAN_DATA_DIR = 1011;
    public static final int JNI_IOCTRL_SET_SCAN_ILLUMINATION_ON_OFF = 762;
    public static final int JNI_IOCTRL_SET_SCAN_TIMEOUT = 761;
    public static final int JNI_SOFTENGINE_IOCTRL_SET_CAMERA_ID = 766;
    public static final int SCN_EVENT_DEC_CANCEL = 2;
    public static final int SCN_EVENT_DEC_SUCC = 1;
    public static final int SCN_EVENT_DEC_TIMEOUT = 4;
    public static final int SCN_EVENT_ERROR = 5;
    public static final int SCN_EVENT_NONE = 0;
    public static final int SCN_EVENT_NO_IMAGE = 3;
    public static final int SCN_EVENT_SCANNER_OVERHEAT = 6;
    private static final String TAG = "ScanJni SoftEngine";
    private static long decodeTime = 0;
    private static SoftEngine mInstance = new SoftEngine();
    private static InterfaceCodeAttrProp mInterfaceCodeAttrProp;
    private static ScanningCallback mScanningCallback;
    private static UpgradeProgressCallback mUpgradeProgressCallback;
    private int FLAG_STATE = 0;
    private final int SIGN_INIT = 1;
    private final int SIGN_OPEN = 16;

    public interface InterfaceCodeAttrProp {
        void onCodeAttrPropCallback(String str, String str2, String str3, String str4, String str5, String str6, int i, String str7);
    }

    public interface ScanningCallback {
        int onScanningCallback(int i, int i2, byte[] bArr, int i3);
    }

    public interface UpgradeProgressCallback {
        void onUpgradeCallback(int i, int i2);
    }

    @Keep
    private native boolean JniClose();

    @Keep
    private native int JniCodeHelpDoc(String str, String str2);

    @Keep
    private native boolean JniDeInit();

    @Keep
    private native String JniGetCodeAttrValue(String str, String str2);

    @Keep
    private native String JniGetHelpDoc(String str);

    @Keep
    private native byte[] JniGetLastImage();

    @Keep
    private native String JniGetVersion(int i);

    @Keep
    private native boolean JniInit() throws DLException;

    @Keep
    private native boolean JniOpen();

    @Keep
    private native int JniScnIOCtrlEx(int i, int i2, Object obj);

    @Keep
    private native int JniSetCodeAttrValue(String str, String str2, String str3);

    @Keep
    private native boolean JniStartDecode(String str, int i);

    private native boolean JniStopDecode(int i);

    static {
        try {
            System.loadLibrary("NlscanHostDecodeJni");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SoftEngine() {
        Log.d(TAG, "new SoftEngine()");
    }

    public static SoftEngine getInstance(Context context) {
        return mInstance;
    }

    public static SoftEngine getInstance() {
        return mInstance;
    }

    public synchronized boolean initSoftEngine(String nlscanDataPath) {
        if (JniScnIOCtrlEx(1011, 0, nlscanDataPath) != 0) {
            return false;
        }
        return initSoftEngine();
    }

    public synchronized boolean initSoftEngine() {
        Log.d(TAG, "initSoftEngine() start");
        if (quickJniInit()) {
            JniScnIOCtrlEx(JNI_IOCTRL_SET_CONTEXT, 0, ScanCamera.getInstance());
            Log.d(TAG, "initSoftEngine() return true");
            return true;
        }
        Log.d(TAG, "initSoftEngine() return false");
        return false;
    }

    public synchronized int setSoftEngineIOCtrlEx(int cmd, int param1, Object obj) {
        return JniScnIOCtrlEx(cmd, param1, obj);
    }

    public synchronized boolean StartDecode() {
        decodeTime = System.currentTimeMillis();
        Log.d(TAG, "App StartDecode()");
        return quickJniStartDecode("sendScanningResultFromNative", 0);
    }

    public synchronized boolean StopDecode() {
        Log.d(TAG, "App StopDecode() ");
        return quickJniStopDecode(0);
    }

    public synchronized boolean Open() {
        Log.d(TAG, "SoftEngine Open() ");
        return quickJniOpen();
    }

    public synchronized boolean Close() {
        Log.d(TAG, "SoftEngine Close() ");
        return quickJniClose();
    }

    public synchronized void setScanningCallback(ScanningCallback scanningCallback) {
        mScanningCallback = scanningCallback;
    }

    @Keep
    public static int sendScanningResultFromNative(int event_code, int msgType, byte[] bMsg1, byte[] bMsg2, int length) {
        Log.d(TAG, "sendScanningResultFromNative");
        return mScanningCallback.onScanningCallback(event_code, msgType, bMsg1, length);
    }

    @Keep
    public static int callBackUpdateProgress(int progressValue, int totalValue) {
        UpgradeProgressCallback upgradeProgressCallback = mUpgradeProgressCallback;
        if (upgradeProgressCallback == null) {
            return 1;
        }
        upgradeProgressCallback.onUpgradeCallback(progressValue, totalValue);
        return 1;
    }

    @Keep
    public synchronized void setUpgradeCallback(UpgradeProgressCallback upgradeCallback) {
        mUpgradeProgressCallback = upgradeCallback;
    }

    public synchronized boolean Deinit() {
        Log.d(TAG, "Deinit() ");
        quickJniClose();
        return quickJniDeInit();
    }

    public synchronized String SDKVersion() {
        return JniGetVersion(JNI_IOCTRL_GET_SDK_VERSION);
    }

    public synchronized String getScannerVersion() {
        return JniGetVersion(JNI_IOCTRL_GET_SCANNER_VERSION);
    }

    public synchronized String getDecodeVersion() {
        return JniGetVersion(JNI_IOCTRL_GET_DECODE_VERSION);
    }

    public synchronized int ScanSet(String Id, String Param1, String Param2) {
        return JniSetCodeAttrValue(Id, Param1, Param2);
    }

    public synchronized String ScanGet(String Id, String Param1) {
        return JniGetCodeAttrValue(Id, Param1);
    }

    public synchronized void setCameraId(int cameraId) {
        JniScnIOCtrlEx(JNI_SOFTENGINE_IOCTRL_SET_CAMERA_ID, cameraId, (Object) null);
    }

    public synchronized byte[] getLastImage() {
        return JniGetLastImage();
    }

    public synchronized int getScannerTemperature() {
        return JniScnIOCtrlEx(JNI_IOCTRL_GET_SCANNER_TEMP, 0, (Object) null);
    }

    public synchronized void setScanTimeout(int timeout) {
        JniScnIOCtrlEx(JNI_IOCTRL_SET_SCAN_TIMEOUT, timeout, (Object) null);
    }

    public synchronized void setIlluminationEnable(int enable) {
        JniScnIOCtrlEx(JNI_IOCTRL_SET_SCAN_ILLUMINATION_ON_OFF, enable, (Object) null);
    }

    public synchronized int getIlluminationEnable() {
        return JniScnIOCtrlEx(JNI_IOCTRL_GET_SCAN_ILLUMINATION_ON_OFF, 0, (Object) null);
    }

    public synchronized int setHighlightEnable(int enable) {
        return JniScnIOCtrlEx(JNI_IOCTRL_SET_HIGHLIGHT_SUPPORT, enable, (Object) null);
    }

    public synchronized int getHighlightEnable() {
        return JniScnIOCtrlEx(JNI_IOCTRL_GET_HIGHLIGHT_SUPPORT, 0, (Object) null);
    }

    public synchronized int setHighlightFrames(int frames) {
        return JniScnIOCtrlEx(JNI_IOCTRL_SET_HIGHLIGHT_FRAMES, frames, (Object) null);
    }

    public synchronized int getHighlightFrames() {
        return JniScnIOCtrlEx(JNI_IOCTRL_GET_HIGHLIGHT_FRAMES, 0, (Object) null);
    }

    public synchronized int setExpectBrightness(int brightness) {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_BRIGHTNESS, brightness, (Object) null);
    }

    public synchronized int getExpectBrightness() {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_BRIGHTNESS, -1, (Object) null);
    }

    public synchronized int setLightTime(int lightTime) {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_LIGHTTIME, lightTime, (Object) null);
    }

    public synchronized int getLightTime() {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_LIGHTTIME, -1, (Object) null);
    }

    public synchronized int setMaxExposure(int exposure) {
        return JniScnIOCtrlEx(JNI_IOCTRL_SETUP_EXPOSURE, exposure, "MAX");
    }

    public synchronized int setMinExposure(int exposure) {
        return JniScnIOCtrlEx(JNI_IOCTRL_SETUP_EXPOSURE, exposure, "MIN");
    }

    public synchronized int getMaxExposure() {
        return JniScnIOCtrlEx(JNI_IOCTRL_SETUP_EXPOSURE, -1, "MAX");
    }

    public synchronized int getMinExposure() {
        return JniScnIOCtrlEx(JNI_IOCTRL_SETUP_EXPOSURE, -1, "MIN");
    }

    public synchronized int setMaxGain(int gain) {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_GAIN, gain, "MAX");
    }

    public synchronized int setMinGain(int gain) {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_GAIN, gain, "MIN");
    }

    public synchronized int getMaxGain() {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_GAIN, -1, "MAX");
    }

    public synchronized int getMinGain() {
        return JniScnIOCtrlEx(ENG_IOCTRL_SETUP_GAIN, -1, "MIN");
    }

    public synchronized void setMulticodeSeparator(byte[] spec) {
        JniScnIOCtrlEx(JNI_IOCTRL_SET_MULTICODE_SEPARATOR, spec.length, spec);
    }

    public synchronized void setNdkSystemLanguage(int langId) {
        JniScnIOCtrlEx(1010, langId, (Object) null);
    }

    public synchronized void setFocusDecodeEnable(int enable) {
        JniScnIOCtrlEx(JNI_IOCTRL_SET_FOCUS_DECODE_ENABLE, enable, (Object) null);
    }

    public synchronized int getFocusDecodeEnable() {
        return JniScnIOCtrlEx(JNI_IOCTRL_GET_FOCUS_DECODE_ENABLE, 0, (Object) null);
    }

    public synchronized void setFocusDecodeCalibration() {
        JniScnIOCtrlEx(JNI_IOCTRL_SET_FOCUS_DECODE_CALIBRATION, 1, (Object) null);
    }

    public int getCodeHelpDoc(String codeName, String attrName) {
        return JniCodeHelpDoc(codeName, attrName);
    }

    @Keep
    public static int callBackCodeAttrProp(String codeName, String fullCodeName, String codeType, String attrName, String attrNickName, String attrType, int value, String propNote) {
        Log.d(TAG, "11setCodeHelpCallback:" + codeName + " " + fullCodeName + " " + codeType + " " + attrName + " " + attrNickName + " " + attrType + " " + value + " " + propNote);
        InterfaceCodeAttrProp interfaceCodeAttrProp = mInterfaceCodeAttrProp;
        if (interfaceCodeAttrProp == null) {
            return 0;
        }
        interfaceCodeAttrProp.onCodeAttrPropCallback(codeName, fullCodeName, codeType, attrName, attrNickName, attrType, value, propNote);
        return 0;
    }

    public void setInterfaceCodeAttrProp(InterfaceCodeAttrProp newInterface) {
        mInterfaceCodeAttrProp = newInterface;
    }

    private boolean quickJniInit() {
        if ((this.FLAG_STATE & 1) == 1) {
            return true;
        }
        try {
            if (JniInit()) {
                this.FLAG_STATE |= 1;
                return true;
            }
            Log.d(TAG, "JNI Init Fail. ");
            return false;
        } catch (DLException e) {
            Log.e(TAG, e.getCode() + e.getReasonPhrase());
            return false;
        }
    }

    private boolean quickJniDeInit() {
        if ((this.FLAG_STATE & 1) != 1) {
            return true;
        }
        if (!JniDeInit()) {
            return false;
        }
        this.FLAG_STATE &= -2;
        return true;
    }

    private boolean quickJniOpen() {
        if ((this.FLAG_STATE & 16) == 16) {
            return true;
        }
        if (!JniOpen()) {
            return false;
        }
        this.FLAG_STATE |= 16;
        return true;
    }

    private boolean quickJniClose() {
        if ((this.FLAG_STATE & 16) != 16) {
            return true;
        }
        if (!JniClose()) {
            return false;
        }
        this.FLAG_STATE &= -17;
        return true;
    }

    private boolean quickJniStartDecode(String callbackFunc, int workMode) {
        if ((this.FLAG_STATE & 16) != 16) {
            return false;
        }
        return JniStartDecode(callbackFunc, workMode);
    }

    private boolean quickJniStopDecode(int workMode) {
        if ((this.FLAG_STATE & 16) != 16) {
            return false;
        }
        return JniStopDecode(workMode);
    }
}
