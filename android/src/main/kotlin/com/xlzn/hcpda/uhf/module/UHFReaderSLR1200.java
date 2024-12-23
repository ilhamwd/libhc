package com.xlzn.hcpda.uhf.module;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.xlzn.hcpda.uhf.UHFReader;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.enums.ConnectState;
import com.xlzn.hcpda.uhf.enums.InventoryModeForPower;
import com.xlzn.hcpda.uhf.enums.LockActionEnum;
import com.xlzn.hcpda.uhf.enums.LockMembankEnum;
import com.xlzn.hcpda.uhf.enums.UHFSession;
import com.xlzn.hcpda.uhf.interfaces.IBuilderAnalysis;
import com.xlzn.hcpda.uhf.interfaces.IUHFCheckCodeErrorCallback;
import com.xlzn.hcpda.uhf.interfaces.IUHFProtocolAnalysis;
import com.xlzn.hcpda.uhf.interfaces.IUHFReader;
import com.xlzn.hcpda.uhf.interfaces.OnInventoryDataListener;
import com.xlzn.hcpda.utils.LoggerUtils;
import java.util.List;

public class UHFReaderSLR1200 extends UHFReaderSLRBase implements IUHFReader {
    /* access modifiers changed from: private */
    public String TAG = "UHFReaderSLR1200";
    private FastModeInventoryThread fastModeInventoryThread = null;
    /* access modifiers changed from: private */
    public PowerSavingModeInventoryThread inventoryThread = null;
    /* access modifiers changed from: private */
    public boolean isMoreTag = false;
    /* access modifiers changed from: private */
    public boolean isTid = false;
    private UHFCheckCodeErrorCallback uhfCheckCodeErrorCallback;

    public /* bridge */ /* synthetic */ UHFReaderResult connect(Context context) {
        return super.connect(context);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult disConnect() {
        return super.disConnect();
    }

    public /* bridge */ /* synthetic */ ConnectState getConnectState() {
        return super.getConnectState();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getFrequencyRegion() {
        return super.getFrequencyRegion();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getPower() {
        return super.getPower();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getSession() {
        return super.getSession();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getTarget() {
        return super.getTarget();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getTemperature() {
        return super.getTemperature();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult getVersions() {
        return super.getVersions();
    }

    public /* bridge */ /* synthetic */ UHFReaderResult kill(String str, SelectEntity selectEntity) {
        return super.kill(str, selectEntity);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult lock(String str, LockMembankEnum lockMembankEnum, LockActionEnum lockActionEnum, SelectEntity selectEntity) {
        return super.lock(str, lockMembankEnum, lockActionEnum, selectEntity);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult read(String str, int i, int i2, int i3, SelectEntity selectEntity) {
        return super.read(str, i, i2, i3, selectEntity);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setBaudRate(int i) {
        return super.setBaudRate(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setDynamicTarget(int i) {
        return super.setDynamicTarget(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setFrequencyPoint(int i) {
        return super.setFrequencyPoint(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setFrequencyRegion(int i) {
        return super.setFrequencyRegion(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setInventoryModeForPower(InventoryModeForPower inventoryModeForPower) {
        return super.setInventoryModeForPower(inventoryModeForPower);
    }

    public /* bridge */ /* synthetic */ void setOnInventoryDataListener(OnInventoryDataListener onInventoryDataListener) {
        super.setOnInventoryDataListener(onInventoryDataListener);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setPower(int i) {
        return super.setPower(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setRFLink(int i) {
        return super.setRFLink(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setSession(UHFSession uHFSession) {
        return super.setSession(uHFSession);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult setStaticTarget(int i) {
        return super.setStaticTarget(i);
    }

    public /* bridge */ /* synthetic */ UHFReaderResult write(String str, int i, int i2, int i3, String str2, SelectEntity selectEntity) {
        return super.write(str, i, i2, i3, str2, selectEntity);
    }

    public UHFReaderSLR1200(IUHFProtocolAnalysis uhfProtocolAnalysisSLR, IBuilderAnalysis builderAnalysisSLR) {
        super(uhfProtocolAnalysisSLR, builderAnalysisSLR);
        UHFCheckCodeErrorCallback uHFCheckCodeErrorCallback = new UHFCheckCodeErrorCallback();
        this.uhfCheckCodeErrorCallback = uHFCheckCodeErrorCallback;
        uhfProtocolAnalysisSLR.setCheckCodeErrorCallback(uHFCheckCodeErrorCallback);
    }

    public UHFReaderResult<Boolean> setInventoryTid(boolean isTid2) {
        this.isTid = isTid2;
        return new UHFReaderResult<>(0, "", true);
    }

    public UHFReaderResult<Boolean> getInventoryTidModel() {
        return new UHFReaderResult<>(0, "", Boolean.valueOf(this.isTid));
    }

    public UHFReaderResult<Boolean> startInventory(SelectEntity selectEntity) {
        byte[] data;
        UHFReaderResult<Boolean> uhfReaderResult;
        this.uhfProtocolAnalysisSLR.cleanTagInfo();
        this.isMoreTag = UHFReader.isMoreTag;
        LoggerUtils.d(this.TAG, "快速模式，  isMoreTag = " + this.isMoreTag);
        if (this.InventoryMode == InventoryModeForPower.POWER_SAVING_MODE) {
            startPowerSavingModeInventoryThread(selectEntity);
            return new UHFReaderResult<>(0, "", true);
        }
        if (this.isTid) {
            LoggerUtils.d(this.TAG, "快速模式，   AA48 要 TID");
            data = this.builderAnalysisSLR.makeStartFastModeInventorySendDataNeedTid512(selectEntity, true);
        } else if (this.isMoreTag) {
            LoggerUtils.d(this.TAG, "快速模式 制作指令，是多标签  AA58");
            data = this.builderAnalysisSLR.makeStartFastModeInventorySendDataMoreTag((SelectEntity) null, false);
        } else {
            LoggerUtils.d(this.TAG, "快速模式，   AA48 不用TID " + this.builderAnalysisSLR.getClass());
            data = this.builderAnalysisSLR.makeStartFastModeInventorySendData(selectEntity, false);
        }
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = sendAndReceiveData(data);
        if (this.isTid) {
            uhfReaderResult = this.builderAnalysisSLR.analysisStartFastModeInventoryReceiveDataNeedTid(dataFrameInfo, true);
        } else if (this.isMoreTag) {
            uhfReaderResult = this.builderAnalysisSLR.analysisStartFastModeInventoryReceiveDataNeedTidMoreTag(dataFrameInfo, false);
        } else {
            uhfReaderResult = this.builderAnalysisSLR.analysisStartFastModeInventoryReceiveData(dataFrameInfo, false);
            LoggerUtils.d(this.TAG, "是我在解析 结果 = " + uhfReaderResult.getResultCode());
        }
        if (uhfReaderResult.getResultCode() != 0) {
            return new UHFReaderResult<>(1, "", false);
        }
        startFastModeInventoryThread();
        return new UHFReaderResult<>(0, "", true);
    }

    public UHFReaderResult<Boolean> stopInventory() {
        if (this.InventoryMode == InventoryModeForPower.POWER_SAVING_MODE) {
            stopPowerSavingModeInventoryThread();
            return new UHFReaderResult<>(0);
        }
        UHFReaderResult<Boolean> uhfReaderResult = this.builderAnalysisSLR.analysisStopFastModeInventoryReceiveData(sendAndReceiveData(this.builderAnalysisSLR.makeStopFastModeInventorySendData()));
        stopFastModeInventoryThread();
        if (uhfReaderResult.getResultCode() != 0) {
            Log.e("ttt", "stopInventory: 停止解析错误 ");
            return new UHFReaderResult<>(1, "", false);
        }
        Log.e("ttt", "stopInventory: 停止解析 ");
        return new UHFReaderResult<>(0, "", true);
    }

    public UHFReaderResult<UHFTagEntity> singleTagInventory(SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisSingleTagInventoryResultData(sendAndReceiveData(this.builderAnalysisSLR.makeSingleTagInventorySendData(selectEntity)));
    }

    public UHFReaderResult<Boolean> setInventorySelectEntity(SelectEntity selectEntity) {
        return this.builderAnalysisSLR.analysisInventorySelectEntityResultData(sendAndReceiveData(this.builderAnalysisSLR.makeInventorySelectEntity(selectEntity)));
    }

    public UHFReaderResult<Boolean> setModuleType(String moduleType) {
        return null;
    }

    public UHFReaderResult<String> getModuleType() {
        return null;
    }

    public void startPowerSavingModeInventoryThread(SelectEntity selectEntity) {
        if (this.inventoryThread == null) {
            PowerSavingModeInventoryThread powerSavingModeInventoryThread = new PowerSavingModeInventoryThread(selectEntity);
            this.inventoryThread = powerSavingModeInventoryThread;
            powerSavingModeInventoryThread.start();
        }
    }

    public void stopPowerSavingModeInventoryThread() {
        PowerSavingModeInventoryThread powerSavingModeInventoryThread = this.inventoryThread;
        if (powerSavingModeInventoryThread != null) {
            powerSavingModeInventoryThread.stopThread();
            this.inventoryThread = null;
        }
    }

    public void startFastModeInventoryThread() {
        if (this.fastModeInventoryThread == null) {
            FastModeInventoryThread fastModeInventoryThread2 = new FastModeInventoryThread();
            this.fastModeInventoryThread = fastModeInventoryThread2;
            fastModeInventoryThread2.start();
        }
    }

    public void stopFastModeInventoryThread() {
        FastModeInventoryThread fastModeInventoryThread2 = this.fastModeInventoryThread;
        if (fastModeInventoryThread2 != null) {
            fastModeInventoryThread2.stopThread();
            this.fastModeInventoryThread = null;
        }
    }

    class FastModeInventoryThread extends Thread {
        private boolean isStop = false;
        private Object lock = new Object();

        FastModeInventoryThread() {
        }

        public void run() {
            List<UHFTagEntity> list;
            while (!this.isStop) {
                UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = UHFReaderSLR1200.this.uhfProtocolAnalysisSLR.getTagInfo();
                if (UHFReaderSLR1200.this.isTid) {
                    list = UHFReaderSLR1200.this.builderAnalysisSLR.analysisFastModeTagInfoReceiveData(dataFrameInfo);
                } else if (UHFReaderSLR1200.this.isMoreTag) {
                    list = UHFReaderSLR1200.this.builderAnalysisSLR.analysisFastModeTagInfoReceiveDataMoreTag(dataFrameInfo);
                } else {
                    list = UHFReaderSLR1200.this.builderAnalysisSLR.analysisFastModeTagInfoReceiveDataOld(dataFrameInfo);
                }
                if (list == null || list.size() <= 0) {
                    sleep(1);
                } else if (UHFReaderSLR1200.this.onInventoryDataListener != null) {
                    UHFReaderSLR1200.this.onInventoryDataListener.onInventoryData(list);
                }
            }
        }

        private void sleep(int time) {
            synchronized (this.lock) {
                try {
                    this.lock.wait((long) time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopThread() {
            this.isStop = true;
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }
    }

    class PowerSavingModeInventoryThread extends Thread {
        private boolean isStop = false;
        private Object lock = new Object();
        private SelectEntity selectEntity = null;
        private long starTime = SystemClock.elapsedRealtime();
        /* access modifiers changed from: private */
        public int tagNumber = 0;
        private long tempTime = SystemClock.elapsedRealtime();

        public PowerSavingModeInventoryThread(SelectEntity selectEntity2) {
            this.selectEntity = selectEntity2;
        }

        public void run() {
            while (!this.isStop) {
                if (this.tagNumber <= 0) {
                    UHFReaderSLR1200 uHFReaderSLR1200 = UHFReaderSLR1200.this;
                    UHFReaderResult<Integer> result = UHFReaderSLR1200.this.builderAnalysisSLR.analysisStartInventoryReceiveData(uHFReaderSLR1200.sendAndReceiveData(uHFReaderSLR1200.builderAnalysisSLR.makeStartInventorySendData(this.selectEntity, UHFReaderSLR1200.this.isTid)));
                    if (result.getResultCode() == 0 && result.getData().intValue() > 0) {
                        this.starTime = SystemClock.elapsedRealtime();
                        this.tagNumber = result.getData().intValue();
                        LoggerUtils.d(UHFReaderSLR1200.this.TAG, "发送盘点命令获取到标签张数:" + result.getData());
                    }
                } else {
                    UHFReaderSLR1200 uHFReaderSLR12002 = UHFReaderSLR1200.this;
                    if (uHFReaderSLR12002.sendData(uHFReaderSLR12002.builderAnalysisSLR.makeGetTagInfoSendData())) {
                        this.tempTime = SystemClock.elapsedRealtime();
                        int k = 0;
                        while (true) {
                            if (k >= 50) {
                                break;
                            }
                            List<UHFTagEntity> list = UHFReaderSLR1200.this.builderAnalysisSLR.analysisTagInfoReceiveData(UHFReaderSLR1200.this.uhfProtocolAnalysisSLR.getTagInfo());
                            if (list == null || list.size() <= 0) {
                                sleep(1);
                                if (SystemClock.elapsedRealtime() - this.tempTime > ((long) 50)) {
                                    break;
                                }
                                k++;
                            } else {
                                if (UHFReaderSLR1200.this.onInventoryDataListener != null) {
                                    UHFReaderSLR1200.this.onInventoryDataListener.onInventoryData(list);
                                }
                                this.tagNumber -= list.size();
                                LoggerUtils.d(UHFReaderSLR1200.this.TAG, "当前获取的标签张数:" + list.size() + "  剩余的标签张数：" + this.tagNumber);
                            }
                        }
                    } else {
                        sleep(10);
                        LoggerUtils.d(UHFReaderSLR1200.this.TAG, "发送数据失败");
                    }
                    if (SystemClock.elapsedRealtime() - this.starTime > 800) {
                        this.tagNumber = 0;
                    }
                }
            }
        }

        private void sleep(int time) {
            synchronized (this.lock) {
                try {
                    this.lock.wait((long) time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopThread() {
            this.isStop = true;
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }
    }

    class UHFCheckCodeErrorCallback implements IUHFCheckCodeErrorCallback {
        UHFCheckCodeErrorCallback() {
        }

        public void checkCodeError(int mode, int cmd, byte[] errorData) {
            LoggerUtils.d(UHFReaderSLR1200.this.TAG, "校验码错误!");
            if (UHFReaderSLR1200.this.inventoryThread != null) {
                int unused = UHFReaderSLR1200.this.inventoryThread.tagNumber = 0;
            }
        }
    }
}
