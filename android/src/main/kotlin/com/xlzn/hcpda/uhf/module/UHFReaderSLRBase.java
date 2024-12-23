package com.xlzn.hcpda.uhf.module;

import android.content.Context;
import android.util.Log;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
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

abstract class UHFReaderSLRBase extends UHFReaderBase implements IUHFReader {
    protected InventoryModeForPower InventoryMode = InventoryModeForPower.FAST_MODE;
    private String TAG = "UHFReaderSLRBase";
    protected IBuilderAnalysis builderAnalysisSLR = null;
    protected OnInventoryDataListener onInventoryDataListener = null;
    protected IUHFProtocolAnalysis uhfProtocolAnalysisSLR = null;

    public UHFReaderSLRBase(IUHFProtocolAnalysis uhfProtocolAnalysisSLR2, IBuilderAnalysis builderAnalysisSLR2) {
        this.uhfProtocolAnalysisSLR = uhfProtocolAnalysisSLR2;
        this.builderAnalysisSLR = builderAnalysisSLR2;
    }

    public UHFReaderResult connect(Context context) {
        return null;
    }

    public UHFReaderResult disConnect() {
        return null;
    }

    public UHFReaderResult<Boolean> setInventoryModeForPower(InventoryModeForPower modeForPower) {
        this.InventoryMode = modeForPower;
        return new UHFReaderResult<>(0, "", true);
    }

    public ConnectState getConnectState() {
        return super.getConnectState();
    }

    public UHFReaderResult<UHFVersionInfo> getVersions() {
        return this.builderAnalysisSLR.analysisVersionData(sendAndReceiveData(this.builderAnalysisSLR.makeGetVersionSendData()));
    }

    public UHFReaderResult<Boolean> setSession(UHFSession vlaue) {
        return this.builderAnalysisSLR.analysisSetSessionResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetSessionSendData(vlaue)));
    }

    public UHFReaderResult<UHFSession> getSession() {
        return this.builderAnalysisSLR.analysisGetSessionResultData(sendAndReceiveData(this.builderAnalysisSLR.makeGetSessionSendData()));
    }

    public UHFReaderResult<Boolean> setPower(int power) {
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = sendAndReceiveData(this.builderAnalysisSLR.makeSetPowerSendData(power));
        int status = dataFrameInfo.status;
        Log.e("ttt", "setPower: status = " + status);
        if (status != 261) {
            return this.builderAnalysisSLR.analysisSetPowerResultData(dataFrameInfo);
        }
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo2 = sendAndReceiveData(this.builderAnalysisSLR.makeSetPowerSendData(30));
        Log.e("ttt", "setPower: 二次 status = " + status);
        return this.builderAnalysisSLR.analysisSetPowerResultData(dataFrameInfo2);
    }

    public UHFReaderResult<Integer> getPower() {
        return this.builderAnalysisSLR.analysisGetPowerResultData(sendAndReceiveData(this.builderAnalysisSLR.makeGetPowerSendData()));
    }

    public UHFReaderResult<Boolean> setFrequencyRegion(int region) {
        return this.builderAnalysisSLR.analysisSetFrequencyRegionResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetFrequencyRegionSendData(region)));
    }

    public UHFReaderResult<Integer> getFrequencyRegion() {
        return this.builderAnalysisSLR.analysisGetFrequencyRegionResultData(sendAndReceiveData(this.builderAnalysisSLR.makeGetFrequencyRegionSendData()));
    }

    public UHFReaderResult<Integer> getTemperature() {
        return this.builderAnalysisSLR.analysisGetTemperatureResultData(sendAndReceiveData(this.builderAnalysisSLR.makeGetTemperatureSendData()));
    }

    public UHFReaderResult<Boolean> setDynamicTarget(int vlaue) {
        return this.builderAnalysisSLR.analysisSetDynamicTargetResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetDynamicTargetSendData(vlaue)));
    }

    public UHFReaderResult<Boolean> setStaticTarget(int vlaue) {
        return this.builderAnalysisSLR.analysisSetStaticTargetResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetStaticTargetSendData(vlaue)));
    }

    public UHFReaderResult<String> read(String password, int membank, int address, int wordCount, SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisReadResultData(sendAndReceiveData(this.builderAnalysisSLR.makeReadSendData(password, membank, address, wordCount, selectEntity)));
    }

    public UHFReaderResult<Boolean> write(String password, int membank, int address, int wordCount, String data, SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisWriteResultData(sendAndReceiveData(this.builderAnalysisSLR.makeWriteSendData(password, membank, address, wordCount, data, selectEntity)));
    }

    public UHFReaderResult<Boolean> kill(String password, SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisKillResultData(sendAndReceiveData(this.builderAnalysisSLR.makeKillSendData(password, selectEntity)));
    }

    public UHFReaderResult<Boolean> lock(String password, LockMembankEnum hexMask, LockActionEnum hexAction, SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisLockResultData(sendAndReceiveData(this.builderAnalysisSLR.makeLockSendData(password, hexMask, hexAction, selectEntity)));
    }

    public UHFReaderResult<int[]> getTarget() {
        return this.builderAnalysisSLR.analysisGetTargetResultData(sendAndReceiveData(this.builderAnalysisSLR.makeGetTargetSendData()));
    }

    public void setOnInventoryDataListener(OnInventoryDataListener onInventoryDataListener2) {
        this.onInventoryDataListener = onInventoryDataListener2;
    }

    public UHFReaderResult<Boolean> setBaudRate(int baudRate) {
        return this.builderAnalysisSLR.analysisSetBaudRateResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetBaudRate(baudRate)));
    }

    public UHFReaderResult<Boolean> setFrequencyPoint(int frequencyPoint) {
        return this.builderAnalysisSLR.analysisSetFrequencyPointResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetFrequencyPoint(frequencyPoint)));
    }

    public UHFReaderResult<Boolean> setRFLink(int mode) {
        Log.e("TAG", "setRFLink: 发送RFLINK");
        return this.builderAnalysisSLR.analysisSetRFLinkResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSetRFLink(mode)));
    }

    /* access modifiers changed from: protected */
    public boolean sendData(byte[] data) {
        return UHFSerialPort.getInstance().send(data);
    }

    /* access modifiers changed from: protected */
    public UHFProtocolAnalysisBase.DataFrameInfo sendAndReceiveData(byte[] sData) {
        if (!sendData(sData)) {
            Log.e("TAG", "这里为空E: ");
            return null;
        }
        return this.uhfProtocolAnalysisSLR.getOtherInfo(sData[2] & 255, 1000);
    }
}
