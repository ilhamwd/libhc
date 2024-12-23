package com.xlzn.hcpda.uhf.analysis;

import android.os.SystemClock;
import android.util.Log;
import com.xlzn.hcpda.ModuleAPI;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.interfaces.IUHFCheckCodeErrorCallback;
import com.xlzn.hcpda.uhf.interfaces.IUHFProtocolAnalysis;
import com.xlzn.hcpda.utils.DataConverter;
import com.xlzn.hcpda.utils.LoggerUtils;
import java.util.Arrays;
import java.util.Iterator;
import kotlin.UByte;

public class UHFProtocolAnalysisSLR extends UHFProtocolAnalysisBase implements IUHFProtocolAnalysis {
    private final int HEADDATA = 255;
    private String TAG = "UHFProtocolAnalysisSLR";
    private IUHFCheckCodeErrorCallback iuhfCheckCodeErrorCallback;
    private Object lock = new Object();
    private byte[] rawPack = null;

    public void analysis(byte[] data) {
        int lastSuccessIndex;
        byte[] bArr = data;
        char c = 0;
        if (this.rawPack == null) {
            this.rawPack = bArr;
        } else {
            LoggerUtils.d(this.TAG, "拼接数据!");
            byte[] bArr2 = this.rawPack;
            byte[] newData = new byte[(bArr2.length + bArr.length)];
            System.arraycopy(bArr2, 0, newData, 0, bArr2.length);
            System.arraycopy(bArr, 0, newData, this.rawPack.length, bArr.length);
            this.rawPack = newData;
        }
        LoggerUtils.d(this.TAG, "原始数据=" + DataConverter.bytesToHex(this.rawPack));
        int lastSuccessIndex2 = 0;
        int index = -1;
        while (this.rawPack.length > index) {
            index++;
            LoggerUtils.d(this.TAG, "analysis index=" + index);
            byte[] bArr3 = this.rawPack;
            if (bArr3.length - index < 7) {
                if (index > 0) {
                    this.rawPack = Arrays.copyOfRange(bArr3, lastSuccessIndex2, bArr3.length);
                    LoggerUtils.d(this.TAG, "数据不完整没有被解析的数据:" + DataConverter.bytesToHex(this.rawPack));
                }
                LoggerUtils.d(this.TAG, "数据不完整没有被解析的数据: index=0");
                return;
            }
            if ((bArr3[index] & 0xFF) == 255) {
                LoggerUtils.d(this.TAG, "校验数据帧");
                int start = index + 1;
                byte[] bArr4 = this.rawPack;
                int dataLen = bArr4[start] & 255;
                if (bArr4.length - (((((index + 1) + 1) + 2) + dataLen) + 2) < 0) {
                    this.rawPack = Arrays.copyOfRange(bArr4, lastSuccessIndex2, bArr4.length);
                    if (LoggerUtils.isDebug()) {
                        LoggerUtils.d(this.TAG, "数据不完整没有被解析的数据:" + DataConverter.bytesToHex(this.rawPack));
                        return;
                    }
                    return;
                }
                int end = dataLen + 4 + start;
                byte[] validateData = Arrays.copyOfRange(bArr4, start, end);
                byte[] crc = Arrays.copyOfRange(this.rawPack, end, end + 2);
                byte[] crcTemp = new byte[2];
                ModuleAPI.getInstance().CalcCRC(validateData, validateData.length, crcTemp);
                Log.d(this.TAG, "数据校验索引! start=" + start + " end=" + end);
                if (crc[c] == crcTemp[c] && crc[1] == crcTemp[1]) {
                    Log.d(this.TAG, "数据校验完成! ");
                    int statusIndex = index + 3;
                    int statusEnd = statusIndex + 2;
                    byte[] status = Arrays.copyOfRange(this.rawPack, statusIndex, statusEnd);
                    byte[] bArr5 = this.rawPack;
                    UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = new UHFProtocolAnalysisBase.DataFrameInfo();
                    dataFrameInfo.command = bArr5[index + 2] & 0xFF;
                    dataFrameInfo.time = SystemClock.elapsedRealtime();
                    int i = lastSuccessIndex2;
                    dataFrameInfo.status = (255 & status[1]) | ((status[0] & 0xFF) << 8);
                    if (dataLen > 0) {
                        dataFrameInfo.data = Arrays.copyOfRange(this.rawPack, statusEnd, statusEnd + dataLen);
                    }
                    addData(dataFrameInfo);
                    byte[] allData = Arrays.copyOfRange(this.rawPack, index, statusEnd + dataLen + 2);
                    if (LoggerUtils.isDebug()) {
                        int i2 = start;
                        LoggerUtils.d(this.TAG, "解析后的整个数据帧 =" + DataConverter.bytesToHex(allData));
                    }
                    index = ((statusEnd + dataLen) + 2) - 1;
                    lastSuccessIndex = index;
                } else {
                    int i3 = lastSuccessIndex2;
                    int i4 = start;
                    lastSuccessIndex = index;
                    LoggerUtils.d(this.TAG, "数据校验出错，重新寻找数据头!");
                    IUHFCheckCodeErrorCallback iUHFCheckCodeErrorCallback = this.iuhfCheckCodeErrorCallback;
                    if (iUHFCheckCodeErrorCallback != null) {
                        iUHFCheckCodeErrorCallback.checkCodeError(0, this.rawPack[index + 2] & 0xFF, (byte[]) null);
                    }
                }
                lastSuccessIndex2 = lastSuccessIndex;
            } else {
                int i5 = lastSuccessIndex2;
                lastSuccessIndex2 = index;
            }
            byte[] bArr6 = this.rawPack;
            if (bArr6.length - 1 == index) {
                if (lastSuccessIndex2 == index) {
                    this.rawPack = null;
                } else {
                    this.rawPack = Arrays.copyOfRange(bArr6, lastSuccessIndex2, bArr6.length);
                    if (LoggerUtils.isDebug()) {
                        LoggerUtils.d(this.TAG, "没有被解析的数据:" + DataConverter.bytesToHex(this.rawPack));
                    }
                }
                LoggerUtils.d(this.TAG, "数据解析完成!");
                return;
            }

            byte[] bArr7 = data;
            c = 0;
        }
    }

    public void setCheckCodeErrorCallback(IUHFCheckCodeErrorCallback iuhfCheckCodeErrorCallback2) {
        this.iuhfCheckCodeErrorCallback = iuhfCheckCodeErrorCallback2;
    }

    public UHFProtocolAnalysisBase.DataFrameInfo getOtherInfo(int cmd, int timeOut) {
        long startTime = SystemClock.uptimeMillis();
        while (SystemClock.uptimeMillis() - startTime < ((long) timeOut)) {
            if (this.listCmd != null && this.listCmd.size() > 0) {
                synchronized (this.lock) {
                    for (int k = 0; k < this.listCmd.size(); k++) {
                        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = (UHFProtocolAnalysisBase.DataFrameInfo) this.listCmd.get(k);
                        if (dataFrameInfo.command == cmd) {
                            this.listCmd.remove(dataFrameInfo);
                            return dataFrameInfo;
                        }
                    }
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG", "getOtherInfo:这里空 " + (SystemClock.uptimeMillis() - startTime < ((long) timeOut)));
        return null;
    }

    /* access modifiers changed from: protected */
    public void addData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo) {
        int tagsTotal;
        if (dataFrameInfo.command == 41) {
            if (dataFrameInfo.status == 0 && (dataFrameInfo.data[3] & 255) > 0) {
                this.queueTaginfo.offer(dataFrameInfo);
            }
        } else if (dataFrameInfo.command == 170) {
            byte[] data = dataFrameInfo.data;
            if (data != null && data.length >= 10) {
                boolean flag = false;
                if ((data[0] & 0xFF) == 77 && (data[1] & 0xFF) == 111 && (data[2] & 0xFF) == 100 && (data[3] & 0xFF) == 117 && (data[4] & 0xFF) == 108 && (data[5] & 0xFF) == 101 && (data[6] & 0xFF) == 116 && (data[7] & 0xFF) == 101 && (data[8] & 0xFF) == 99 && (data[9] & 0xFF) == 104) {
                    flag = true;
                }
                if (flag) {
                    addOtherInfoData(dataFrameInfo);
                } else if (dataFrameInfo.status == 0 && (tagsTotal = dataFrameInfo.data[3] & 255) > 0) {
                    LoggerUtils.d(this.TAG, "增加标签数据.标签个数 = " + tagsTotal);
                    this.queueTaginfo.offer(dataFrameInfo);
                }
            }
        } else {
            addOtherInfoData(dataFrameInfo);
        }
    }

    private void addOtherInfoData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo) {
        synchronized (this.lock) {
            Iterator<UHFProtocolAnalysisBase.DataFrameInfo> iterator = this.listCmd.iterator();
            while (iterator.hasNext()) {
                if (SystemClock.elapsedRealtime() - iterator.next().time > 5000) {
                    iterator.remove();
                }
            }
            this.listCmd.add(dataFrameInfo);
        }
    }
}
