package com.xlzn.hcpda.uhf.module;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.hc.so.HcPowerCtrl;
import com.xlzn.hcpda.DeviceConfigManage;
import com.xlzn.hcpda.uhf.analysis.BuilderAnalysisSLR;
import com.xlzn.hcpda.uhf.analysis.BuilderAnalysisSLR_E710;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisSLR;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.entity.UHFVersionInfo;
import com.xlzn.hcpda.uhf.enums.ConnectState;
import com.xlzn.hcpda.uhf.enums.InventoryModeForPower;
import com.xlzn.hcpda.uhf.enums.LockActionEnum;
import com.xlzn.hcpda.uhf.enums.LockMembankEnum;
import com.xlzn.hcpda.uhf.enums.UHFSession;
import com.xlzn.hcpda.uhf.interfaces.IBuilderAnalysis;
import com.xlzn.hcpda.uhf.interfaces.IUHFProtocolAnalysis;
import com.xlzn.hcpda.uhf.interfaces.IUHFReader;
import com.xlzn.hcpda.uhf.interfaces.OnInventoryDataListener;
import com.xlzn.hcpda.uhf.serialport.UHFSerialPort;
import com.xlzn.hcpda.utils.DataConverter;
import com.xlzn.hcpda.utils.LoggerUtils;

public class UHFReaderSLR implements IUHFReader {
    public static boolean is5300 = false;
    public static boolean isR2000 = false;
    public static boolean isWK0 = true;
    public static UHFReaderSLR uhfReaderSLR = new UHFReaderSLR();
    private String TAG = "UHFReaderSLR";
    private IBuilderAnalysis builderAnalysisSLR = new BuilderAnalysisSLR();
    private HcPowerCtrl hcPowerCtrl = new HcPowerCtrl();
    private IUHFReader iuhfReader = null;
    private IUHFProtocolAnalysis uhfProtocolAnalysisSLR = new UHFProtocolAnalysisSLR();

    public static UHFReaderSLR getInstance() {
        return uhfReaderSLR;
    }

    public UHFReaderResult<Boolean> setInventoryTid(boolean flag) {
        return this.iuhfReader.setInventoryTid(flag);
    }

    public UHFReaderResult<Boolean> getInventoryTidModel() {
        return this.iuhfReader.getInventoryTidModel();
    }

    public UHFReaderResult<Boolean> connect(Context context) {
        LoggerUtils.d(this.TAG, "connect!");
        boolean z = false;
        if (getConnectState() == ConnectState.CONNECTED) {
            LoggerUtils.d(this.TAG, "模块已经连接成功!");
            return new UHFReaderResult<>(0, "模块已经连接成功,不可重复连接!", true);
        }
        DeviceConfigManage.UHFConfig uhfConfig = DeviceConfigManage.getInstance().getUhfConfig();
        if (isWK0) {
            this.hcPowerCtrl.uhfPower(1);
            this.hcPowerCtrl.uhfCtrl(1);
        } else {
            this.hcPowerCtrl.identityCtrl(1);
            this.hcPowerCtrl.identityPower(1);
        }
        LoggerUtils.d(this.TAG, "供电-------9");
        UHFReaderResult<UHFVersionInfo> verInfo = null;
        int k = 0;
        while (true) {
            if (k >= 2) {
                UHFReaderResult<UHFVersionInfo> uHFReaderResult = verInfo;
                int i = k;
                break;
            }
            boolean result = UHFSerialPort.getInstance().open(uhfConfig.getUhfUart(), this.uhfProtocolAnalysisSLR, 115200);
            LoggerUtils.d(this.TAG, "打开串口=" + result + "  Uart=" + uhfConfig.getUhfUart() + "  baudrate=" + 115200);
            if (!result) {
                LoggerUtils.d(this.TAG, "打开串口失败!");
                return new UHFReaderResult<>(3, UHFReaderResult.ResultMessage.OPEN_SERIAL_PORT_FAILURE, z);
            }
            SystemClock.sleep(180);
            UHFReaderResult<UHFVersionInfo> uHFReaderResult2 = verInfo;
            sendData(DataConverter.hexToBytes("FF00041D0B"));
            SystemClock.sleep(300);
            boolean z2 = result;
            LoggerUtils.d(this.TAG, "获取版本号0!");
            UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = sendAndReceiveData(this.builderAnalysisSLR.makeGetVersionSendData());
            UHFReaderResult<UHFVersionInfo> verInfo2 = this.builderAnalysisSLR.analysisVersionData(dataFrameInfo);
            UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo2 = dataFrameInfo;
            int k2 = k;
            Boolean bool = z;
            LoggerUtils.d(this.TAG, "唤醒模块----" + verInfo2.getResultCode());
            SystemClock.sleep(200);
            if (verInfo2.getResultCode() == 0) {
                if (verInfo2.getData().getHardwareVersion().startsWith("32") || verInfo2.getData().getHardwareVersion().startsWith("31") || verInfo2.getData().getHardwareVersion().startsWith("33")) {
                    this.builderAnalysisSLR = new BuilderAnalysisSLR_E710();
                    LoggerUtils.d(this.TAG, "是E710啊----");
                    DeviceConfigManage.module_type = "E710";
                } else {
                    LoggerUtils.d(this.TAG, "不是E710啊----");
                }
                verInfo = verInfo2;
                break;
            } else {
                LoggerUtils.d(this.TAG, "获取版本号失败，打开另外一个串口");
                SystemClock.sleep(100);
                if (isWK0) {
                    uhfConfig.setUhfUart("/dev/ttysWK0");
                } else {
                    uhfConfig.setUhfUart("/dev/ttysWK1");
                }
                UHFReaderResult<UHFVersionInfo> uHFReaderResult3 = verInfo2;
                boolean result2 = UHFSerialPort.getInstance().open(uhfConfig.getUhfUart(), this.uhfProtocolAnalysisSLR, 115200);
                LoggerUtils.d(this.TAG, "打开串口=" + result2 + "  Uart=" + uhfConfig.getUhfUart() + "  baudrate=" + 115200);
                if (!result2) {
                    LoggerUtils.d(this.TAG, "打开串口失败!");
                    return new UHFReaderResult<>(3, UHFReaderResult.ResultMessage.OPEN_SERIAL_PORT_FAILURE, bool);
                }
                Boolean bool2 = bool;
                SystemClock.sleep(80);
                sendData(DataConverter.hexToBytes("FF00041D0B"));
                SystemClock.sleep(400);
                LoggerUtils.d(this.TAG, "获取版本号!");
                UHFReaderResult<UHFVersionInfo> verInfo3 = this.builderAnalysisSLR.analysisVersionData(sendAndReceiveData(this.builderAnalysisSLR.makeGetVersionSendData()));
                SystemClock.sleep(200);
                if (verInfo3.getResultCode() == 0) {
                    LoggerUtils.d(this.TAG, "模版版本----" + verInfo3.getData().getHardwareVersion());
                    if (verInfo3.getData().getHardwareVersion().startsWith("32") || verInfo3.getData().getHardwareVersion().startsWith("31") || verInfo3.getData().getHardwareVersion().startsWith("33")) {
                        this.builderAnalysisSLR = new BuilderAnalysisSLR_E710();
                        LoggerUtils.d(this.TAG, "是E710啊----");
                        DeviceConfigManage.module_type = "E710";
                    } else {
                        LoggerUtils.d(this.TAG, " 非EE710");
                    }
                    verInfo = verInfo3;
                } else {
                    LoggerUtils.d(this.TAG, "获取版本号失败");
                    UHFSerialPort.getInstance().close();
                    z = bool2;
                    k = k2 + 1;
                    verInfo = verInfo3;
                }
            }
        }
        Log.d("ResultCode", "Result code = " + verInfo.getResultCode());
        if (verInfo.getResultCode() != 0) {
            disConnect();
            return new UHFReaderResult<>(1, "获取模块信息失败!");
        }
        UHFVersionInfo uhfVersionInfo = verInfo.getData();
        String hver = uhfVersionInfo.getHardwareVersion();
        LoggerUtils.d(this.TAG, "固件版本:" + uhfVersionInfo.getFirmwareVersion() + "  硬件版本=" + hver);
        if (hver.startsWith("A1")) {
            LoggerUtils.d(this.TAG, "R2000 协议构建");
            DeviceConfigManage.module_type = "R2000";
            is5300 = false;
            isR2000 = true;
            this.iuhfReader = new UHFReaderSLR1200(this.uhfProtocolAnalysisSLR, this.builderAnalysisSLR);
        } else if (hver.startsWith("31") || hver.startsWith("33") || hver.startsWith("32")) {
            LoggerUtils.d(this.TAG, "E710 协议构建");
            if (hver.startsWith("33")) {
                DeviceConfigManage.module_type = "E310";
            } else {
                DeviceConfigManage.module_type = "E710";
            }
            if (hver.startsWith("32")) {
                DeviceConfigManage.module_type = "E510";
            }
            isR2000 = false;
            is5300 = false;
            this.iuhfReader = new UHFReaderSLR1200(this.uhfProtocolAnalysisSLR, this.builderAnalysisSLR);
        } else if (hver.startsWith("A6") || hver.startsWith("A3")) {
            LoggerUtils.d(this.TAG, "5300 协议构建");
            if (hver.startsWith("A6")) {
                DeviceConfigManage.module_type = "5100";
            } else {
                DeviceConfigManage.module_type = "5300";
            }
            is5300 = true;
            this.iuhfReader = new UHFReaderSLR1200(this.uhfProtocolAnalysisSLR, this.builderAnalysisSLR);
        }
        IUHFReader iUHFReader = this.iuhfReader;
        if (iUHFReader == null) {
            return new UHFReaderResult<>(1);
        }
        ((UHFReaderBase) iUHFReader).setConnectState(ConnectState.CONNECTED);
        return new UHFReaderResult<>(0);
    }

    public UHFReaderResult disConnect() {
        stopInventory();
        this.hcPowerCtrl.uhfPower(0);
        this.hcPowerCtrl.uhfCtrl(0);
        this.hcPowerCtrl.identityCtrl(0);
        this.hcPowerCtrl.identityPower(0);
        LoggerUtils.d("CHLOG", "----------------------模块下电");
        UHFSerialPort.getInstance().close();
        IUHFReader iUHFReader = this.iuhfReader;
        if (iUHFReader != null) {
            ((UHFReaderBase) iUHFReader).setConnectState(ConnectState.DISCONNECT);
        }
        return new UHFReaderResult(0);
    }

    public UHFReaderResult<Boolean> startInventory(SelectEntity selectEntity) {
        return this.iuhfReader.startInventory(selectEntity);
    }

    public UHFReaderResult<Boolean> stopInventory() {
        IUHFReader iUHFReader = this.iuhfReader;
        if (iUHFReader != null) {
            return iUHFReader.stopInventory();
        }
        Log.e("TAG", "stopInventory在这里返回了: ");
        return new UHFReaderResult<>(1, "");
    }

    public UHFReaderResult<UHFTagEntity> singleTagInventory(SelectEntity selectEntity) {
        return this.iuhfReader.singleTagInventory(selectEntity);
    }

    public UHFReaderResult<Boolean> setInventorySelectEntity(SelectEntity selectEntity) {
        return this.iuhfReader.setInventorySelectEntity(selectEntity);
    }

    public ConnectState getConnectState() {
        IUHFReader iUHFReader = this.iuhfReader;
        if (iUHFReader == null) {
            return ConnectState.DISCONNECT;
        }
        return iUHFReader.getConnectState();
    }

    public UHFReaderResult<UHFVersionInfo> getVersions() {
        return this.iuhfReader.getVersions();
    }

    public UHFReaderResult<Boolean> setSession(UHFSession vlaue) {
        return this.iuhfReader.setSession(vlaue);
    }

    public UHFReaderResult<UHFSession> getSession() {
        return this.iuhfReader.getSession();
    }

    public UHFReaderResult<Boolean> setDynamicTarget(int vlaue) {
        return this.iuhfReader.setDynamicTarget(vlaue);
    }

    public UHFReaderResult<Boolean> setStaticTarget(int vlaue) {
        return this.iuhfReader.setStaticTarget(vlaue);
    }

    public UHFReaderResult<int[]> getTarget() {
        return this.iuhfReader.getTarget();
    }

    public UHFReaderResult<Boolean> setInventoryModeForPower(InventoryModeForPower modeForPower) {
        return this.iuhfReader.setInventoryModeForPower(modeForPower);
    }

    public void setOnInventoryDataListener(OnInventoryDataListener onInventoryDataListener) {
        this.iuhfReader.setOnInventoryDataListener(onInventoryDataListener);
    }

    public UHFReaderResult<Boolean> setPower(int power) {
        return this.iuhfReader.setPower(power);
    }

    public UHFReaderResult<Integer> getPower() {
        return this.iuhfReader.getPower();
    }

    public UHFReaderResult<Boolean> setModuleType(String moduleType) {
        return null;
    }

    public UHFReaderResult<String> getModuleType() {
        return null;
    }

    public UHFReaderResult<Boolean> setFrequencyRegion(int region) {
        return this.iuhfReader.setFrequencyRegion(region);
    }

    public UHFReaderResult<Integer> getFrequencyRegion() {
        return this.iuhfReader.getFrequencyRegion();
    }

    public UHFReaderResult<Integer> getTemperature() {
        return this.iuhfReader.getTemperature();
    }

    public UHFReaderResult<String> read(String password, int membank, int address, int wordCount, SelectEntity selectEntity) {
        return this.iuhfReader.read(password, membank, address, wordCount, selectEntity);
    }

    public UHFReaderResult<Boolean> write(String password, int membank, int address, int wordCount, String data, SelectEntity selectEntity) {
        return this.iuhfReader.write(password, membank, address, wordCount, data, selectEntity);
    }

    public UHFReaderResult<Boolean> kill(String password, SelectEntity selectEntity) {
        return this.iuhfReader.kill(password, selectEntity);
    }

    public UHFReaderResult<Boolean> lock(String password, LockMembankEnum hexMask, LockActionEnum hexAction, SelectEntity selectEntity) {
        return this.iuhfReader.lock(password, hexMask, hexAction, selectEntity);
    }

    public UHFReaderResult<Boolean> setBaudRate(int baudRate) {
        return this.iuhfReader.setBaudRate(baudRate);
    }

    public UHFReaderResult<Boolean> setFrequencyPoint(int baudRate) {
        return this.iuhfReader.setFrequencyPoint(baudRate);
    }

    public UHFReaderResult<Boolean> setRFLink(int mode) {
        return this.iuhfReader.setRFLink(mode);
    }

    private boolean sendData(byte[] data) {
        return UHFSerialPort.getInstance().send(data);
    }

    private UHFProtocolAnalysisBase.DataFrameInfo sendAndReceiveData(byte[] sData) {
        if (!sendData(sData)) {
            return null;
        }
        return this.uhfProtocolAnalysisSLR.getOtherInfo(sData[2] & 255, 1000);
    }
}
