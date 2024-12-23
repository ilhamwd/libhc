package com.dawn.decoderapijni;

import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.Log;
import com.dawn.decoderapijni.SoftEngine;
import com.dawn.decoderapijni.bean.AttrHelpBean;
import com.dawn.decoderapijni.bean.CodeEnableBean;
import com.dawn.decoderapijni.bean.PropValueHelpBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceTools {
    public static final int MSG_INIT_DONE = 5652;
    public static final int MSG_INIT_FIRMWARE_UPGRADE = 5653;
    private static ServiceTools mInstance = new ServiceTools();
    private final int SCAN_SET_SCAN_UPDATE = 705;
    private final String TAG = "dLog_tools";
    /* access modifiers changed from: private */
    public Set<String> codeSet = new HashSet();
    /* access modifiers changed from: private */
    public boolean initStatus = false;
    /* access modifiers changed from: private */
    public List<AttrHelpBean> listAttrHelpBean = new ArrayList();
    /* access modifiers changed from: private */
    public List<CodeEnableBean> listCodeEnable1D = new ArrayList();
    /* access modifiers changed from: private */
    public List<CodeEnableBean> listCodeEnable2D = new ArrayList();
    /* access modifiers changed from: private */
    public List<CodeEnableBean> listCodeEnableOther = new ArrayList();
    /* access modifiers changed from: private */
    public List<PropValueHelpBean> listPropHelpBean = new ArrayList();

    private ServiceTools() {
        Log.d("dLog_tools", "new ServiceTools()");
    }

    public static ServiceTools getInstance() {
        return mInstance;
    }

    public boolean getInitStatus() {
        return this.initStatus;
    }

    public void startInit(final String nlscanDataPath, final boolean autoUpdateFirmwareFlag, @NonNull final Handler msgHandler) {
        if (!this.initStatus) {
            new Thread(new Runnable() {
                public void run() {
                    Log.d("dLog_tools", "ScanInit_Runnable run");
                    if (SoftEngine.getInstance().initSoftEngine(nlscanDataPath)) {
                        SoftEngine.getInstance().Open();
                        boolean unused = ServiceTools.this.initStatus = true;
                        if (autoUpdateFirmwareFlag && ServiceTools.this.updateFirmwareByPathAndVersion("/sdcard/bin", (String) null, msgHandler)) {
                            SoftEngine.getInstance().Deinit();
                            boolean unused2 = ServiceTools.this.initStatus = false;
                            if (SoftEngine.getInstance().initSoftEngine(nlscanDataPath)) {
                                SoftEngine.getInstance().Open();
                                boolean unused3 = ServiceTools.this.initStatus = true;
                            }
                        }
                        ServiceTools.this.getCodeEnableList();
                    }
                    msgHandler.sendEmptyMessage(ServiceTools.MSG_INIT_DONE);
                }
            }).start();
        }
    }

    public void deInit() {
        SoftEngine.getInstance().StopDecode();
        SoftEngine.getInstance().Close();
        SoftEngine.getInstance().Deinit();
        this.initStatus = false;
    }

    public void startScan() {
        SoftEngine.getInstance().StartDecode();
    }

    public void stopScan() {
        SoftEngine.getInstance().StopDecode();
    }

    public boolean updateFirmware(String fwFileAbsPath) {
        FileChannel inChannel = null;
        File fwFile = new File(fwFileAbsPath);
        boolean z = false;
        try {
            inChannel = new FileInputStream(fwFile).getChannel();
            int fwLen = (int) fwFile.length();
            ByteBuffer inBuffer = ByteBuffer.allocate(fwLen);
            if (fwLen != inChannel.read(inBuffer)) {
                if (inChannel != null) {
                    try {
                        inChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
            if (SoftEngine.getInstance().setSoftEngineIOCtrlEx(705, fwLen, inBuffer.array()) == 0) {
                z = true;
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return z;
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
//            if (inChannel != null) {
//                inChannel.close();
//            }
            return false;
        } catch (IOException e4) {
            e4.printStackTrace();
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th) {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }

    private boolean checkVersion(String binVersion) {
        String nowVersion = SoftEngine.getInstance().getScannerVersion();
        if (nowVersion == null || nowVersion.indexOf(binVersion) <= 0) {
            return false;
        }
        return true;
    }

    public boolean updateFirmwareByPathAndVersion(String path, String version, @NonNull Handler msgHandler) {
        File filePath = new File(path);
        if (!filePath.exists() || !filePath.isDirectory()) {
            Log.e("dLog_tools", "File Path is not exists!! " + path);
            return false;
        }
        File[] files = filePath.listFiles();
        if (files.length == 0) {
            Log.e("dLog_tools", "no found .bin, skip update...");
            return false;
        }
        String binName = null;
        String binVersion = null;
        if (version != null) {
            int length = files.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                File bin = files[i];
                if (bin.getName().indexOf(version) > 0) {
                    binName = bin.getAbsolutePath();
                    binVersion = version;
                    break;
                }
                i++;
            }
        } else if (files.length != 1) {
            Log.e("dLog_tools", "too many.. bin");
            return false;
        } else {
            binName = files[0].getAbsolutePath();
            String fileName = files[0].getName();
            int index = fileName.indexOf("_V");
            binVersion = fileName.substring(index + 1, index + 1 + 9);
        }
        if (binName == null) {
            return false;
        }
        Log.d("dLog_tools", "Firmware name is:" + binName + " version:" + binVersion);
        if (checkVersion(binVersion)) {
            Log.w("dLog_tools", "no need to update!!");
            return false;
        }
        msgHandler.sendEmptyMessage(MSG_INIT_FIRMWARE_UPGRADE);
        for (int tryNum = 0; tryNum < 3; tryNum++) {
            if (updateFirmware(binName) && getInitStatus()) {
                if (checkVersion(binVersion)) {
                    Log.w("dLog_tools", "update success!!");
                    return true;
                }
                Log.w("dLog_tools", "update try again..");
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void getCodeEnableList() {
        this.listCodeEnable1D.clear();
        this.listCodeEnable2D.clear();
        this.listCodeEnableOther.clear();
        this.codeSet.clear();
        setCodeEnableListCallback();
        SoftEngine.getInstance().getCodeHelpDoc("ALL", "Enable");
        Collections.reverse(this.listCodeEnable1D);
        Collections.reverse(this.listCodeEnable2D);
        Collections.reverse(this.listCodeEnableOther);
    }

    public List<CodeEnableBean> get1DCodeEnableList() {
        return this.listCodeEnable1D;
    }

    public List<CodeEnableBean> get2DCodeEnableList() {
        return this.listCodeEnable2D;
    }

    public List<CodeEnableBean> getOtherCodeEnableList() {
        return this.listCodeEnableOther;
    }

    public List<AttrHelpBean> getAttrHelpsBeans(String codeName) {
        this.listAttrHelpBean.clear();
        setAttrBeanListCallback();
        SoftEngine.getInstance().getCodeHelpDoc(codeName, "ALL");
        Collections.reverse(this.listAttrHelpBean);
        return this.listAttrHelpBean;
    }

    public List<PropValueHelpBean> getPropHelpsBeans(String codeName, String attrName) {
        this.listPropHelpBean.clear();
        setPropBeanListCallback();
        SoftEngine.getInstance().getCodeHelpDoc(codeName, attrName);
        Collections.reverse(this.listPropHelpBean);
        return this.listPropHelpBean;
    }

    private void setCodeEnableListCallback() {
        SoftEngine.getInstance().setInterfaceCodeAttrProp(new SoftEngine.InterfaceCodeAttrProp() {
            public void onCodeAttrPropCallback(String codeName, String fullCodeName, String codeType, String attrName, String attrNickName, String attrType, int value, String propNote) {
                String str = codeName;
                String str2 = codeType;
                int i = value;
                if (str != null) {
                    char c = 65535;
                    int hashCode = codeType.hashCode();
                    if (hashCode != 1587) {
                        if (hashCode != 1618) {
                            if (hashCode == 2461856 && str2.equals("POST")) {
                                c = 2;
                            }
                        } else if (str2.equals("2D")) {
                            c = 1;
                        }
                    } else if (str2.equals("1D")) {
                        c = 0;
                    }
                    if (c != 0) {
                        if (c != 1) {
                            if (c != 2) {
                                Log.e("dLog_tools", "codeType: " + str2);
                            } else if (!ServiceTools.this.codeSet.contains(str)) {
                                ServiceTools.this.codeSet.add(str);
                                List access$500 = ServiceTools.this.listCodeEnableOther;
                                access$500.add(new CodeEnableBean(codeName, fullCodeName, codeType, attrName, attrNickName, attrType, i + "", propNote));
                            }
                        } else if (!ServiceTools.this.codeSet.contains(str)) {
                            ServiceTools.this.codeSet.add(str);
                            List access$400 = ServiceTools.this.listCodeEnable2D;
                            access$400.add(new CodeEnableBean(codeName, fullCodeName, codeType, attrName, attrNickName, attrType, i + "", propNote));
                        }
                    } else if (!ServiceTools.this.codeSet.contains(str)) {
                        ServiceTools.this.codeSet.add(str);
                        List access$300 = ServiceTools.this.listCodeEnable1D;
                        access$300.add(new CodeEnableBean(codeName, fullCodeName, codeType, attrName, attrNickName, attrType, i + "", propNote));
                    }
                }
            }
        });
    }

    private void setAttrBeanListCallback() {
        SoftEngine.getInstance().setInterfaceCodeAttrProp(new SoftEngine.InterfaceCodeAttrProp() {
            public void onCodeAttrPropCallback(String codeName, String fullCodeName, String codeType, String attrName, String attrNickName, String attrType, int value, String propNote) {
                ServiceTools.this.listAttrHelpBean.add(new AttrHelpBean(attrName, attrNickName, attrType, value, propNote));
            }
        });
    }

    private void setPropBeanListCallback() {
        SoftEngine.getInstance().setInterfaceCodeAttrProp(new SoftEngine.InterfaceCodeAttrProp() {
            public void onCodeAttrPropCallback(String codeName, String fullCodeName, String codeType, String attrName, String attrNickName, String attrType, int value, String propNote) {
                ServiceTools.this.listPropHelpBean.add(new PropValueHelpBean(value, propNote));
            }
        });
    }

    public byte[] getImageLast() {
        return SoftEngine.getInstance().getLastImage();
    }
}
