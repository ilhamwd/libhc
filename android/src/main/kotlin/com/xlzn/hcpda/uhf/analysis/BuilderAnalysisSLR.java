package com.xlzn.hcpda.uhf.analysis;

import android.util.Log;
import com.xlzn.hcpda.ModuleAPI;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.entity.UHFVersionInfo;
import com.xlzn.hcpda.uhf.enums.LockActionEnum;
import com.xlzn.hcpda.uhf.enums.LockMembankEnum;
import com.xlzn.hcpda.uhf.enums.UHFSession;
import com.xlzn.hcpda.uhf.interfaces.IBuilderAnalysis;
import com.xlzn.hcpda.uhf.module.UHFReaderSLR;
import com.xlzn.hcpda.utils.DataConverter;
import com.xlzn.hcpda.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.UByte;

public class BuilderAnalysisSLR implements IBuilderAnalysis {
    private String TAG = "BuilderAnalysisSLR";
    public boolean isAB = false;
    public boolean isTID = false;

    public byte[] makeSingleTagInventorySendData(SelectEntity selectEntity) {
        if (selectEntity == null) {
            return buildSendData(33, new byte[]{1, -24, 16, 0, 6});
        }
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        byte[] data = new byte[(len + 10)];
        data[0] = 1;
        data[1] = -24;
        data[2] = (byte) (selectEntity.getOption() + 16);
        data[3] = 0;
        data[4] = 6;
        int address = selectEntity.getAddress();
        data[5] = (byte) ((address >> 24) & 255);
        data[6] = (byte) ((address >> 16) & 255);
        data[7] = (byte) ((address >> 8) & 255);
        data[8] = (byte) (address & 255);
        data[9] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        for (int k = 0; k < len; k++) {
            data[k + 10] = byteData[k];
        }
        return buildSendData(33, data);
    }

    public UHFReaderResult<UHFTagEntity> analysisSingleTagInventoryResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", null);
        }
        byte[] bytes = data.data;
        byte rssi = bytes[3];
        int ant = (bytes[4] & 0xFF) >> 4;
        byte[] epcbytes = Arrays.copyOfRange(bytes, 5, bytes.length - 2);
        UHFTagEntity uhfTagEntity = new UHFTagEntity();
        uhfTagEntity.setEcpHex(DataConverter.bytesToHex(epcbytes));
        uhfTagEntity.setCount(1);
        uhfTagEntity.setAnt(ant);
        uhfTagEntity.setRssi(rssi);
        return new UHFReaderResult<>(0, "", uhfTagEntity);
    }

    public byte[] makeInventorySelectEntity(SelectEntity selectEntity) {
        byte[] senddata = new byte[512];
        byte[] moduletech = "Moduletech".getBytes();
        System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
        int index = moduletech.length;
        int subcrcIndex = index;
        int index2 = index + 1;
        senddata[index] = -86;
        int index3 = index2 + 1;
        senddata[index2] = 76;
        int index4 = index3 + 1;
        senddata[index3] = -1;
        int index5 = index4 + 1;
        senddata[index4] = -1;
        int index6 = index5 + 1;
        senddata[index5] = 1;
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        int index7 = index6 + 1;
        senddata[index6] = (byte) (len + 7);
        int selBANK = selectEntity.getOption();
        if (selBANK == 4) {
            selBANK = 1;
        }
        int index8 = index7 + 1;
        senddata[index7] = (byte) selBANK;
        int address = selectEntity.getAddress();
        int index9 = index8 + 1;
        senddata[index8] = (byte) ((address >> 24) & 255);
        int index10 = index9 + 1;
        senddata[index9] = (byte) ((address >> 16) & 255);
        int index11 = index10 + 1;
        senddata[index10] = (byte) ((address >> 8) & 255);
        int index12 = index11 + 1;
        senddata[index11] = (byte) (address & 255);
        int index13 = index12 + 1;
        senddata[index12] = (byte) selectEntity.getLength();
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k = 0;
        while (k < len) {
            senddata[index13] = byteData[k];
            k++;
            index13++;
        }
        int subcrcTemp = 0;
        for (int k2 = subcrcIndex; k2 < index13; k2++) {
            subcrcTemp += senddata[k2] & 0xFF;
        }
        int k3 = index13 + 1;
        senddata[index13] = (byte) (subcrcTemp & 255);
        senddata[k3] = -69;
        return buildSendData(170, Arrays.copyOf(senddata, k3 + 1));
    }

    public UHFReaderResult<Boolean> analysisInventorySelectEntityResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "设置连续盘点指定标签指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 76) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public byte[] makeSetTargetModel(int model) {
        return buildSendData(5, new byte[]{1, 0, 0});
    }

    public byte[] makeStartInventorySendData(SelectEntity selectEntity, boolean isTID2) {
        boolean z = isTID2;
        LoggerUtils.d(this.TAG, "开启普通盘点模式 isTID=" + z);
        this.isTID = z;
        if (selectEntity != null) {
            int len = selectEntity.getLength() / 8;
            if (selectEntity.getLength() % 8 != 0) {
                len++;
            }
            byte[] data = new byte[(len + 14)];
            data[0] = (byte) selectEntity.getOption();
            data[1] = 0;
            data[2] = 0;
            data[3] = 0;
            data[4] = -106;
            data[5] = 0;
            data[6] = 0;
            data[7] = 0;
            data[8] = 0;
            int address = selectEntity.getAddress();
            data[9] = (byte) ((address >> 24) & 255);
            data[10] = (byte) ((address >> 16) & 255);
            data[11] = (byte) ((address >> 8) & 255);
            data[12] = (byte) (address & 255);
            data[13] = (byte) selectEntity.getLength();
            byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
            for (int k = 0; k < len; k++) {
                data[k + 14] = byteData[k];
            }
            return buildSendData(34, data);
        } else if (z) {
            LoggerUtils.d(this.TAG, "开启普通盘点模式 盘点TID");
            return buildSendData(34, new byte[]{0, 0, 4, 0, -106, 1, 9, 40, 0, 0, 0, 2, 0, 0, 0, 0, 6});
        } else {
            return buildSendData(34, new byte[]{0, 0, 0, 0, -106});
        }
    }

    public UHFReaderResult<Integer> analysisStartInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        int tagCount;
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        if (((data.data[2] & 0xFF) >> 4) == 1) {
            tagCount = (data.data[6] & 0xFF) + UByte.MIN_VALUE;
        } else {
            tagCount = data.data[3] & 0xFF;
        }
        return new UHFReaderResult<>(0, "", Integer.valueOf(tagCount));
    }

    public byte[] makeStartFastModeInventorySendData(SelectEntity selectEntity, boolean isTID2) {
        int index;
        int index2;
        LoggerUtils.d(this.TAG, "来的这里------");
        if (selectEntity == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index3 = moduletech.length;
            int subcrcIndex = index3;
            int index4 = index3 + 1;
            senddata[index3] = -86;
            int index5 = index4 + 1;
            senddata[index4] = 72;
            if (isTID2) {
                int index6 = index5 + 1;
                senddata[index5] = 0;
                int index7 = index6 + 1;
                senddata[index6] = -121;
                int index8 = index7 + 1;
                senddata[index7] = 0;
                index = index8 + 1;
                senddata[index8] = 16;
            } else {
                int index9 = index5 + 1;
                senddata[index5] = 0;
                int index10 = index9 + 1;
                senddata[index9] = 6;
                int index11 = index10 + 1;
                senddata[index10] = 0;
                index = index11 + 1;
                senddata[index11] = -112;
            }
            if (!isTID2) {
                index2 = index + 1;
                senddata[index] = 3;
            } else {
                int index12 = index + 1;
                senddata[index] = 4;
                int index13 = index12 + 1;
                senddata[index12] = 1;
                int index14 = index13 + 1;
                senddata[index13] = 9;
                int index15 = index14 + 1;
                senddata[index14] = 40;
                int index16 = index15 + 1;
                senddata[index15] = 0;
                int index17 = index16 + 1;
                senddata[index16] = 0;
                int index18 = index17 + 1;
                senddata[index17] = 0;
                int index19 = index18 + 1;
                senddata[index18] = 2;
                int index20 = index19 + 1;
                senddata[index19] = 0;
                int index21 = index20 + 1;
                senddata[index20] = 0;
                int index22 = index21 + 1;
                senddata[index21] = 0;
                int index23 = index22 + 1;
                senddata[index22] = 0;
                index2 = index23 + 1;
                senddata[index23] = 6;
            }
            int subcrcTemp = 0;
            for (int k = subcrcIndex; k < index2; k++) {
                subcrcTemp += senddata[k] & 0xFF;
            }
            int k2 = index2 + 1;
            senddata[index2] = (byte) (subcrcTemp & 255);
            senddata[k2] = -69;
            return buildSendData(170, Arrays.copyOf(senddata, k2 + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index24 = moduletech2.length;
        int subcrcIndex2 = index24;
        int index25 = index24 + 1;
        senddata2[index24] = -86;
        int index26 = index25 + 1;
        senddata2[index25] = 72;
        int index27 = index26 + 1;
        senddata2[index26] = 0;
        int index28 = index27 + 1;
        senddata2[index27] = 7;
        int index29 = index28 + 1;
        senddata2[index28] = (byte) selectEntity.getOption();
        int index30 = index29 + 1;
        senddata2[index29] = 16;
        int index31 = index30 + 1;
        senddata2[index30] = 0;
        int index32 = index31 + 1;
        senddata2[index31] = 0;
        int index33 = index32 + 1;
        senddata2[index32] = 0;
        int index34 = index33 + 1;
        senddata2[index33] = 0;
        int index35 = index34 + 1;
        senddata2[index34] = 0;
        int address = selectEntity.getAddress();
        int index36 = index35 + 1;
        senddata2[index35] = (byte) ((address >> 24) & 255);
        int index37 = index36 + 1;
        senddata2[index36] = (byte) ((address >> 16) & 255);
        int index38 = index37 + 1;
        senddata2[index37] = (byte) ((address >> 8) & 255);
        int index39 = index38 + 1;
        senddata2[index38] = (byte) (address & 255);
        int index40 = index39 + 1;
        senddata2[index39] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k3 = 0;
        while (k3 < len) {
            senddata2[index40] = byteData[k3];
            k3++;
            index40++;
        }
        int subcrcTemp2 = 0;
        for (int k4 = subcrcIndex2; k4 < index40; k4++) {
            subcrcTemp2 += senddata2[k4] & 0xFF;
        }
        int index41 = index40 + 1;
        senddata2[index40] = (byte) (subcrcTemp2 & 255);
        senddata2[index41] = -69;
        return buildSendData(170, Arrays.copyOf(senddata2, index41 + 1));
    }

    public byte[] makeStartFastModeInventorySendDataMoreTag(SelectEntity selectEntity, boolean isTID2) {
        int index;
        if (selectEntity == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index2 = moduletech.length;
            int subcrcIndex = index2;
            int index3 = index2 + 1;
            senddata[index2] = -86;
            int index4 = index3 + 1;
            senddata[index3] = 88;
            int k = 0;
            while (k < 20) {
                senddata[index4] = 0;
                k++;
                index4++;
            }
            int index5 = index4 + 1;
            senddata[index4] = 0;
            int index6 = index5 + 1;
            senddata[index5] = 6;
            int index7 = index6 + 1;
            senddata[index6] = 0;
            int index8 = index7 + 1;
            senddata[index7] = 16;
            if (!isTID2) {
                LoggerUtils.d(this.TAG, "不需要TID");
                index = index8 + 1;
                senddata[index8] = 0;
            } else {
                int index9 = index8 + 1;
                senddata[index8] = 4;
                int index10 = index9 + 1;
                senddata[index9] = 1;
                int index11 = index10 + 1;
                senddata[index10] = 9;
                int index12 = index11 + 1;
                senddata[index11] = 40;
                int index13 = index12 + 1;
                senddata[index12] = 0;
                int index14 = index13 + 1;
                senddata[index13] = 0;
                int index15 = index14 + 1;
                senddata[index14] = 0;
                int index16 = index15 + 1;
                senddata[index15] = 2;
                int index17 = index16 + 1;
                senddata[index16] = 0;
                int index18 = index17 + 1;
                senddata[index17] = 0;
                int index19 = index18 + 1;
                senddata[index18] = 0;
                int index20 = index19 + 1;
                senddata[index19] = 0;
                index = index20 + 1;
                senddata[index20] = 6;
            }
            int subcrcTemp = 0;
            for (int k2 = subcrcIndex; k2 < index; k2++) {
                subcrcTemp += senddata[k2] & 0xFF;
            }
            int k3 = index + 1;
            senddata[index] = (byte) (subcrcTemp & 255);
            senddata[k3] = -69;
            LoggerUtils.d(this.TAG, "构建快速模式指令发出,不需要过滤 AA58");
            byte[] bArr = new byte[512];
            return buildSendData(170, Arrays.copyOf(senddata, k3 + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index21 = moduletech2.length;
        int subcrcIndex2 = index21;
        int index22 = index21 + 1;
        senddata2[index21] = -86;
        int index23 = index22 + 1;
        senddata2[index22] = 88;
        int k4 = 0;
        while (k4 < 20) {
            senddata2[index23] = 0;
            k4++;
            index23++;
        }
        int index24 = index23 + 1;
        senddata2[index23] = 0;
        int index25 = index24 + 1;
        senddata2[index24] = -121;
        int index26 = index25 + 1;
        senddata2[index25] = (byte) selectEntity.getOption();
        int index27 = index26 + 1;
        senddata2[index26] = 16;
        int index28 = index27 + 1;
        senddata2[index27] = 0;
        int index29 = index28 + 1;
        senddata2[index28] = 0;
        int index30 = index29 + 1;
        senddata2[index29] = 0;
        int index31 = index30 + 1;
        senddata2[index30] = 0;
        int index32 = index31 + 1;
        senddata2[index31] = 0;
        int address = selectEntity.getAddress();
        int index33 = index32 + 1;
        senddata2[index32] = (byte) ((address >> 24) & 255);
        int index34 = index33 + 1;
        senddata2[index33] = (byte) ((address >> 16) & 255);
        int index35 = index34 + 1;
        senddata2[index34] = (byte) ((address >> 8) & 255);
        int index36 = index35 + 1;
        senddata2[index35] = (byte) (address & 255);
        int index37 = index36 + 1;
        senddata2[index36] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k5 = 0;
        while (k5 < len) {
            senddata2[index37] = byteData[k5];
            k5++;
            index37++;
        }
        int subcrcTemp2 = 0;
        for (int k6 = subcrcIndex2; k6 < index37; k6++) {
            subcrcTemp2 += senddata2[k6] & 0xFF;
        }
        int k7 = index37 + 1;
        senddata2[index37] = (byte) (subcrcTemp2 & 255);
        int index38 = k7 + 1;
        senddata2[k7] = -69;
        LoggerUtils.d(this.TAG, "构建快速模式指令发出,需要过滤 AA58");
        return buildSendData(170, Arrays.copyOf(senddata2, index38));
    }

    public byte[] makeStartFastModeInventorySendDataNeedTid(SelectEntity selectEntity, boolean isTID2) {
        int index;
        int index2;
        LoggerUtils.d(this.TAG, "我在这---------------");
        if (!UHFReaderSLR.isR2000 && selectEntity != null && isTID2) {
            return makeStartFastModeInventorySendDataNeedTid3(selectEntity, isTID2);
        }
        if (selectEntity == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index3 = moduletech.length;
            int subcrcIndex = index3;
            int index4 = index3 + 1;
            senddata[index3] = -86;
            int index5 = index4 + 1;
            senddata[index4] = 72;
            if (isTID2) {
                int index6 = index5 + 1;
                senddata[index5] = 0;
                int index7 = index6 + 1;
                senddata[index6] = -121;
                int index8 = index7 + 1;
                senddata[index7] = 0;
                index = index8 + 1;
                senddata[index8] = 16;
            } else {
                int index9 = index5 + 1;
                senddata[index5] = 0;
                int index10 = index9 + 1;
                senddata[index9] = 6;
                int index11 = index10 + 1;
                senddata[index10] = 0;
                index = index11 + 1;
                senddata[index11] = -112;
            }
            if (!isTID2) {
                index2 = index + 1;
                senddata[index] = 3;
            } else {
                int index12 = index + 1;
                senddata[index] = 4;
                int index13 = index12 + 1;
                senddata[index12] = 1;
                int index14 = index13 + 1;
                senddata[index13] = 9;
                int index15 = index14 + 1;
                senddata[index14] = 40;
                int index16 = index15 + 1;
                senddata[index15] = 0;
                int index17 = index16 + 1;
                senddata[index16] = 0;
                int index18 = index17 + 1;
                senddata[index17] = 0;
                int index19 = index18 + 1;
                senddata[index18] = 2;
                int index20 = index19 + 1;
                senddata[index19] = 0;
                int index21 = index20 + 1;
                senddata[index20] = 0;
                int index22 = index21 + 1;
                senddata[index21] = 0;
                int index23 = index22 + 1;
                senddata[index22] = 0;
                index2 = index23 + 1;
                senddata[index23] = 6;
            }
            int subcrcTemp = 0;
            for (int k = subcrcIndex; k < index2; k++) {
                subcrcTemp += senddata[k] & 0xFF;
            }
            int k2 = index2 + 1;
            senddata[index2] = (byte) (subcrcTemp & 255);
            senddata[k2] = -69;
            return buildSendData(170, Arrays.copyOf(senddata, k2 + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index24 = moduletech2.length;
        int subcrcIndex2 = index24;
        int index25 = index24 + 1;
        senddata2[index24] = -86;
        int index26 = index25 + 1;
        senddata2[index25] = 72;
        int index27 = index26 + 1;
        senddata2[index26] = 0;
        int index28 = index27 + 1;
        senddata2[index27] = 7;
        int index29 = index28 + 1;
        senddata2[index28] = (byte) selectEntity.getOption();
        int index30 = index29 + 1;
        senddata2[index29] = 16;
        int index31 = index30 + 1;
        senddata2[index30] = 0;
        int index32 = index31 + 1;
        senddata2[index31] = 0;
        int index33 = index32 + 1;
        senddata2[index32] = 0;
        int index34 = index33 + 1;
        senddata2[index33] = 0;
        int index35 = index34 + 1;
        senddata2[index34] = 0;
        int address = selectEntity.getAddress();
        int index36 = index35 + 1;
        senddata2[index35] = (byte) ((address >> 24) & 255);
        int index37 = index36 + 1;
        senddata2[index36] = (byte) ((address >> 16) & 255);
        int index38 = index37 + 1;
        senddata2[index37] = (byte) ((address >> 8) & 255);
        int index39 = index38 + 1;
        senddata2[index38] = (byte) (address & 255);
        int index40 = index39 + 1;
        senddata2[index39] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k3 = 0;
        while (k3 < len) {
            senddata2[index40] = byteData[k3];
            k3++;
            index40++;
        }
        int subcrcTemp2 = 0;
        for (int k4 = subcrcIndex2; k4 < index40; k4++) {
            subcrcTemp2 += senddata2[k4] & 0xFF;
        }
        int index41 = index40 + 1;
        senddata2[index40] = (byte) (subcrcTemp2 & 255);
        senddata2[index41] = -69;
        return buildSendData(170, Arrays.copyOf(senddata2, index41 + 1));
    }

    public byte[] makeStartFastModeInventorySendDataNeedTid512(SelectEntity selectEntity, boolean isTID2) {
        return new byte[0];
    }

    public byte[] makeStartFastModeInventorySendDataNeedTid3(SelectEntity selectEntity, boolean isTID2) {
        if (selectEntity == null) {
            return null;
        }
        byte[] senddata = new byte[512];
        byte[] moduletech = "Moduletech".getBytes();
        System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
        int index = moduletech.length;
        int subcrcIndex = index;
        int index2 = index + 1;
        senddata[index] = -86;
        int index3 = index2 + 1;
        senddata[index2] = 72;
        int index4 = index3 + 1;
        senddata[index3] = 0;
        int index5 = index4 + 1;
        senddata[index4] = -121;
        int index6 = index5 + 1;
        senddata[index5] = (byte) selectEntity.getOption();
        int index7 = index6 + 1;
        senddata[index6] = 0;
        int index8 = index7 + 1;
        senddata[index7] = 4;
        int index9 = index8 + 1;
        senddata[index8] = 0;
        int index10 = index9 + 1;
        senddata[index9] = 0;
        int index11 = index10 + 1;
        senddata[index10] = 0;
        int index12 = index11 + 1;
        senddata[index11] = 0;
        int address = selectEntity.getAddress();
        int index13 = index12 + 1;
        senddata[index12] = (byte) ((address >> 24) & 255);
        int index14 = index13 + 1;
        senddata[index13] = (byte) ((address >> 16) & 255);
        int index15 = index14 + 1;
        senddata[index14] = (byte) ((address >> 8) & 255);
        int index16 = index15 + 1;
        senddata[index15] = (byte) (address & 255);
        int index17 = index16 + 1;
        senddata[index16] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k = 0;
        while (k < len) {
            senddata[index17] = byteData[k];
            k++;
            index17++;
        }
        int k2 = index17 + 1;
        senddata[index17] = 1;
        int index18 = k2 + 1;
        senddata[k2] = 9;
        int index19 = index18 + 1;
        senddata[index18] = 40;
        int index20 = index19 + 1;
        senddata[index19] = 0;
        int index21 = index20 + 1;
        senddata[index20] = 0;
        int index22 = index21 + 1;
        senddata[index21] = 0;
        int index23 = index22 + 1;
        senddata[index22] = 2;
        int index24 = index23 + 1;
        senddata[index23] = 0;
        int index25 = index24 + 1;
        senddata[index24] = 0;
        int index26 = index25 + 1;
        senddata[index25] = 0;
        int index27 = index26 + 1;
        senddata[index26] = 0;
        int index28 = index27 + 1;
        senddata[index27] = 6;
        int subcrcTemp = 0;
        for (int k3 = subcrcIndex; k3 < index28; k3++) {
            subcrcTemp += senddata[k3] & 0xFF;
        }
        int k4 = index28 + 1;
        senddata[index28] = (byte) (subcrcTemp & 255);
        senddata[k4] = -69;
        return buildSendData(170, Arrays.copyOf(senddata, k4 + 1));
    }

    public List<UHFTagEntity> analysisFastModeTagInfoReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = data;
        if (dataFrameInfo == null || dataFrameInfo.status != 0) {
            return null;
        }
        LoggerUtils.d(this.TAG, "解析盘点数据：" + DataConverter.bytesToHex(dataFrameInfo.data));
        byte[] taginfo = dataFrameInfo.data;
        List<UHFTagEntity> list = new ArrayList<>();
        byte statIndex = taginfo[2];
        int userLen = ((int) (Long.parseLong(DataConverter.bytesToHex(Arrays.copyOfRange(taginfo, 3, 5)), 16) / 8)) - 12;
        LoggerUtils.d(this.TAG, "user 数据长度 = " + userLen);
        int starAdd = 2 + 1 + 2;
        int endAdd = starAdd + 12;
        byte[] tidBytes = Arrays.copyOfRange(taginfo, starAdd, endAdd);
        int statIndex2 = endAdd;
        LoggerUtils.d(this.TAG, " TID = " + DataConverter.bytesToHex(tidBytes));
        int endAdd2 = userLen + statIndex2;
        byte[] userBytes = Arrays.copyOfRange(taginfo, statIndex2, endAdd2);
        int statIndex3 = endAdd2;
        LoggerUtils.d(this.TAG, " USER = " + DataConverter.bytesToHex(userBytes));
        int statIndex4 = statIndex3 + 1;
        byte epcLen = taginfo[statIndex3];
        int endAdd3 = Integer.parseInt(String.valueOf(epcLen), 10);
        LoggerUtils.d(this.TAG, " epcLen = " + epcLen);
        LoggerUtils.d(this.TAG, " statIndex  = " + statIndex4);
        LoggerUtils.d(this.TAG, " endAdd = " + endAdd3);
        int epcEnd = endAdd3 + statIndex4;
        byte b = epcLen;
        LoggerUtils.d(this.TAG, " epcEnd = " + epcEnd);
        byte[] epcBytes = Arrays.copyOfRange(taginfo, statIndex4 + 2, epcEnd - 2);
        LoggerUtils.d(this.TAG, " EPC = " + DataConverter.bytesToHex(epcBytes));
        String epc = DataConverter.bytesToHex(epcBytes);
        String tid = DataConverter.bytesToHex(tidBytes);
        String user = DataConverter.bytesToHex(userBytes);
        byte[] bArr = taginfo;
        UHFTagEntity uhfTagEntity = new UHFTagEntity();
        uhfTagEntity.setRssi(statIndex);
        uhfTagEntity.setEcpHex(epc);
        uhfTagEntity.setTidHex(tid);
        byte b2 = statIndex;
        uhfTagEntity.setCount(1);
        uhfTagEntity.setUserHex(user);
        list.add(uhfTagEntity);
        UHFTagEntity uHFTagEntity = uhfTagEntity;
        LoggerUtils.d(this.TAG, "--------------------------------------- " + epcEnd);
        return list;
    }

    public List<UHFTagEntity> analysisFastModeTagInfoReceiveDataOld(UHFProtocolAnalysisBase.DataFrameInfo data) {
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = data;
        if (dataFrameInfo == null || dataFrameInfo.status != 0) {
            return null;
        }
        LoggerUtils.d(this.TAG, "解析盘点数据：2222222222 = " + DataConverter.bytesToHex(dataFrameInfo.data));
        byte[] taginfo = dataFrameInfo.data;
        int i = 2;
        int tagsTotal = taginfo[2] & 255;
        int rssi = 3;
        List<UHFTagEntity> list = new ArrayList<>();
        int k = 0;
        while (k < tagsTotal) {
            int statIndex = rssi + 1;
            byte statIndex2 = taginfo[rssi];
            int statIndex3 = statIndex + 1;
            int ant = (taginfo[statIndex] & 0xFF) >> 4;
            byte[] tidBytes = null;
            int statIndex4 = statIndex3 + 1;
            int statIndex5 = statIndex4 + 1;
            int tidLen = ((taginfo[statIndex3] & 0xFF) << 8) | (taginfo[statIndex4] & 255);
            if (tidLen > 0) {
                int starAdd = statIndex5;
                int endAdd = starAdd + (tidLen / 8);
                tidBytes = Arrays.copyOfRange(taginfo, starAdd, endAdd);
                statIndex5 = endAdd;
            }
            UHFTagEntity uhfTagEntity = new UHFTagEntity();
            if (tidBytes != null) {
                uhfTagEntity.setTidHex(DataConverter.bytesToHex(tidBytes));
            } else if (this.isTID) {
                rssi = statIndex5;
                k++;
                UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo2 = data;
                i = 2;
            }
            int statIndex6 = statIndex5 + 1;
            byte[] pcBytes = new byte[i];
            int statIndex7 = statIndex6 + 1;
            pcBytes[0] = taginfo[statIndex6];
            int statIndex8 = statIndex7 + 1;
            pcBytes[1] = taginfo[statIndex7];
            int epcIdLen = ((taginfo[statIndex5] & 255) - 2) - 2;
            byte[] epcBytes = new byte[epcIdLen];
            int m = 0;
            while (m < epcIdLen) {
                epcBytes[m] = taginfo[statIndex8];
                m++;
                statIndex8++;
            }
            int statIndex9 = statIndex8 + 2;
            uhfTagEntity.setAnt(ant);
            uhfTagEntity.setRssi(statIndex2);
            uhfTagEntity.setCount(1);
            uhfTagEntity.setEcpHex(DataConverter.bytesToHex(epcBytes));
            if (uhfTagEntity.getEcpHex() == null) {
                uhfTagEntity.setEcpHex("");
            }
            uhfTagEntity.setPcHex(DataConverter.bytesToHex(pcBytes));
            list.add(uhfTagEntity);
            rssi = statIndex9;
            k++;
            UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo22 = data;
            i = 2;
        }
        if (list.size() < tagsTotal) {
            LoggerUtils.d(this.TAG, "解析盘点数据异常 tagsTotal：" + tagsTotal + "  list.size()=" + list.size());
        }
        return list;
    }

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID2) {
        this.isTID = isTID2;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 72) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataNeedTid(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID2) {
        this.isTID = isTID2;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 72) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataNeedTidMoreTag(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID2) {
        this.isTID = isTID2;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "E710开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 88) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public byte[] makeStopFastModeInventorySendData() {
        byte[] data = new byte[14];
        byte[] moduletech = "Moduletech".getBytes();
        System.arraycopy(moduletech, 0, data, 0, moduletech.length);
        data[10] = -86;
        data[11] = 73;
        data[12] = (byte) (243 & 255);
        data[13] = -69;
        return buildSendData(170, data);
    }

    public UHFReaderResult<Boolean> analysisStopFastModeInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "停止盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 73) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        Log.e("TAG", "analysisStopFastModeInventoryReceiveData: 查询22 = " + data);
        return new UHFReaderResult<>(1, "", false);
    }

    public byte[] makeGetTagInfoSendData() {
        return buildSendData(41, new byte[]{0, -121, 0});
    }

    public List<UHFTagEntity> analysisTagInfoReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        byte[] taginfo;
        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo = data;
        if (dataFrameInfo == null) {
            return null;
        } else if (dataFrameInfo.status == 0) {
            byte[] taginfo2 = dataFrameInfo.data;
            int tagsTotal = taginfo2[3] & 255;
            int statIndex = 4;
            List<UHFTagEntity> list = new ArrayList<>();
            int k = 0;
            while (k < tagsTotal) {
                int count = taginfo2[statIndex] & 255;
                int statIndex2 = statIndex + 1;
                byte rssi = taginfo2[statIndex2];
                int statIndex3 = statIndex2 + 1;
                int ant = (taginfo2[statIndex3] & 0xFF) >> 4;
                byte[] tidBytes = null;
                int statIndex4 = statIndex3 + 1;
                int statIndex5 = statIndex4 + 1;
                int tidLen = ((taginfo2[statIndex4] & 0xFF) << 8) | (taginfo2[statIndex5] & 255);
                if (tidLen > 0) {
                    int starAdd = statIndex5 + 1;
                    int endAdd = starAdd + (tidLen / 8);
                    tidBytes = Arrays.copyOfRange(taginfo2, starAdd, endAdd);
                    statIndex5 = endAdd - 1;
                }
                int statIndex6 = statIndex5 + 1;
                int statIndex7 = statIndex6 + 1;
                int statIndex8 = statIndex7 + 1;
                int statIndex9 = statIndex8 + 1;
                byte[] pcBytes = {taginfo2[statIndex8], taginfo2[statIndex9]};
                int epcIdLen = (((((taginfo2[statIndex6] & 0xFF) << 8) | (taginfo2[statIndex7] & 255)) / 8) - 2) - 2;
                byte[] epcBytes = new byte[epcIdLen];
                for (int m = 0; m < epcIdLen; m++) {
                    statIndex9++;
                    epcBytes[m] = taginfo2[statIndex9];
                }
                statIndex = statIndex9 + 3;
                UHFTagEntity uhfTagEntity = new UHFTagEntity();
                if (tidBytes != null) {
                    uhfTagEntity.setTidHex(DataConverter.bytesToHex(tidBytes));
                    taginfo = taginfo2;
                } else {
                    taginfo = taginfo2;
                    if (this.isTID) {
                        k++;
                        UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo2 = data;
                        taginfo2 = taginfo;
                    }
                }
                uhfTagEntity.setAnt(ant);
                uhfTagEntity.setRssi(rssi);
                uhfTagEntity.setCount(count);
                uhfTagEntity.setEcpHex(DataConverter.bytesToHex(epcBytes));
                if (uhfTagEntity.getEcpHex() == null) {
                    uhfTagEntity.setEcpHex("");
                }
                uhfTagEntity.setPcHex(DataConverter.bytesToHex(pcBytes));
                list.add(uhfTagEntity);
                k++;
                UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo22 = data;
                taginfo2 = taginfo;
            }
            return list;
        } else {
            return null;
        }
    }

    public byte[] makeGetVersionSendData() {
        return buildSendData(3, (byte[]) null);
    }

    public UHFReaderResult<UHFVersionInfo> analysisVersionData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        byte[] version = data.data;
        UHFVersionInfo versionInfo = new UHFVersionInfo();
        versionInfo.setFirmwareVersion(DataConverter.bytesToHex(Arrays.copyOfRange(version, 8, 12)));
        versionInfo.setHardwareVersion(DataConverter.bytesToHex(Arrays.copyOfRange(version, 4, 8)));
        LoggerUtils.d(this.TAG, "固件版本:" + versionInfo.getFirmwareVersion());
        LoggerUtils.d(this.TAG, "硬件版本:" + versionInfo.getHardwareVersion());
        return new UHFReaderResult<>(0, "", versionInfo);
    }

    public byte[] makeSetSessionSendData(UHFSession value) {
        return buildSendData(155, new byte[]{5, 0, (byte) value.getValue()});
    }

    public UHFReaderResult<Boolean> analysisSetSessionResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            LoggerUtils.d(this.TAG, "SetSession fail");
            return new UHFReaderResult<>(1);
        }
        LoggerUtils.d(this.TAG, "SetSession success");
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeGetSessionSendData() {
        return buildSendData(107, new byte[]{5, 0});
    }

    public UHFReaderResult<UHFSession> analysisGetSessionResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data != null && data.status == 0 && data.data[0] == 5 && data.data[1] == 0) {
            return new UHFReaderResult<>(0, "", UHFSession.getValue(data.data[2]));
        }
        return new UHFReaderResult<>(1);
    }

    public byte[] makeSetPowerSendData(int power) {
        int power2 = power * 100;
        return buildSendData(145, new byte[]{3, 1, (byte) ((power2 >> 8) & 255), (byte) (power2 & 255), (byte) ((power2 >> 8) & 255), (byte) (power2 & 255)});
    }

    public UHFReaderResult<Boolean> analysisSetPowerResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", false);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeGetPowerSendData() {
        return buildSendData(97, new byte[]{3});
    }

    public UHFReaderResult<Integer> analysisGetPowerResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", 0);
        }
        byte[] temp = data.data;
        int power = (((temp[2] & 0xFF) << 8) | (temp[3] & 0xFF)) / 100;
        LoggerUtils.d(this.TAG, "power=" + power);
        return new UHFReaderResult<>(0, "", Integer.valueOf(power));
    }

    public byte[] makeSetFrequencyRegionSendData(int FrequencyRegion) {
        return buildSendData(151, new byte[]{(byte) FrequencyRegion});
    }

    public UHFReaderResult<Boolean> analysisSetFrequencyRegionResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", false);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeGetFrequencyRegionSendData() {
        return buildSendData(103, (byte[]) null);
    }

    public UHFReaderResult<Integer> analysisGetFrequencyRegionResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", -1);
        }
        return new UHFReaderResult<>(0, "", Integer.valueOf(data.data[0] & 255));
    }

    public byte[] makeGetTemperatureSendData() {
        return buildSendData(114, (byte[]) null);
    }

    public UHFReaderResult<Integer> analysisGetTemperatureResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", 0);
        }
        return new UHFReaderResult<>(0, "", Integer.valueOf(data.data[0]));
    }

    public byte[] makeSetDynamicTargetSendData(int value) {
        byte[] data = {5, 1, 0, (byte) value};
        this.isAB = true;
        return buildSendData(155, data);
    }

    public UHFReaderResult<Boolean> analysisSetDynamicTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", false);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeSetStaticTargetSendData(int value) {
        return buildSendData(155, new byte[]{5, 1, 1, (byte) value});
    }

    public UHFReaderResult<Boolean> analysisSetStaticTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1, "", false);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeGetTargetSendData() {
        return buildSendData(107, new byte[]{5, 1});
    }

    public UHFReaderResult<int[]> analysisGetTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0 || data.data[0] != 5 || data.data[1] != 1) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", new int[]{data.data[2], data.data[3]});
    }

    public byte[] makeReadSendData(String password, int membank, int address, int wordCount, SelectEntity selectEntity) {
        int i = membank;
        int i2 = address;
        int i3 = wordCount;
        if (selectEntity == null) {
            byte[] pwd = DataConverter.hexToBytes(password);
            return buildSendData(40, new byte[]{7, -48, 5, (byte) i, (byte) ((i2 >> 24) & 255), (byte) ((i2 >> 16) & 255), (byte) ((i2 >> 8) & 255), (byte) (i2 & 255), (byte) i3, pwd[0], pwd[1], pwd[2], pwd[3]});
        }
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        byte[] data = new byte[(len + 18)];
        data[0] = 3;
        data[1] = -24;
        data[2] = (byte) selectEntity.getOption();
        data[3] = (byte) i;
        data[4] = (byte) ((i2 >> 24) & 255);
        data[5] = (byte) ((i2 >> 16) & 255);
        data[6] = (byte) ((i2 >> 8) & 255);
        data[7] = (byte) (i2 & 255);
        data[8] = (byte) i3;
        byte[] pwd2 = DataConverter.hexToBytes(password);
        data[9] = pwd2[0];
        data[10] = pwd2[1];
        data[11] = pwd2[2];
        data[12] = pwd2[3];
        data[13] = (byte) ((selectEntity.getAddress() >> 24) & 255);
        data[14] = (byte) ((selectEntity.getAddress() >> 16) & 255);
        data[15] = (byte) ((selectEntity.getAddress() >> 8) & 255);
        data[16] = (byte) (selectEntity.getAddress() & 255);
        data[17] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        for (int k = 0; k < len; k++) {
            data[k + 18] = byteData[k];
        }
        return buildSendData(40, data);
    }

    public UHFReaderResult<String> analysisReadResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", DataConverter.bytesToHex(Arrays.copyOfRange(data.data, 1, data.data.length)));
    }

    public byte[] makeWriteSendData(String password, int membank, int address, int wordCount, String hexData, SelectEntity selectEntity) {
        String hexData2;
        int i = membank;
        int i2 = address;
        int i3 = wordCount;
        if (hexData.length() / 4 > i3) {
            hexData2 = hexData.substring(0, i3 * 4);
        } else {
            hexData2 = hexData;
        }
        if (selectEntity == null) {
            byte[] byteData = DataConverter.hexToBytes(hexData2);
            byte[] data = new byte[(byteData.length + 12)];
            data[0] = 7;
            data[1] = -48;
            data[2] = 5;
            data[3] = (byte) ((i2 >> 24) & 255);
            data[4] = (byte) ((i2 >> 16) & 255);
            data[5] = (byte) ((i2 >> 8) & 255);
            data[6] = (byte) (i2 & 255);
            data[7] = (byte) i;
            byte[] pwd = DataConverter.hexToBytes(password);
            data[8] = pwd[0];
            data[9] = pwd[1];
            data[10] = pwd[2];
            data[11] = pwd[3];
            for (int k = 0; k < byteData.length; k++) {
                data[k + 12] = byteData[k];
            }
            return buildSendData(36, data);
        }
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        byte[] byteData2 = DataConverter.hexToBytes(hexData2);
        byte[] data2 = new byte[(byteData2.length + 17 + len)];
        data2[0] = 3;
        data2[1] = -24;
        data2[2] = (byte) selectEntity.getOption();
        data2[3] = (byte) ((i2 >> 24) & 255);
        data2[4] = (byte) ((i2 >> 16) & 255);
        data2[5] = (byte) ((i2 >> 8) & 255);
        data2[6] = (byte) (i2 & 255);
        data2[7] = (byte) i;
        byte[] pwd2 = DataConverter.hexToBytes(password);
        data2[8] = pwd2[0];
        data2[9] = pwd2[1];
        data2[10] = pwd2[2];
        data2[11] = pwd2[3];
        data2[12] = (byte) ((selectEntity.getAddress() >> 24) & 255);
        data2[13] = (byte) ((selectEntity.getAddress() >> 16) & 255);
        data2[14] = (byte) ((selectEntity.getAddress() >> 8) & 255);
        data2[15] = (byte) (selectEntity.getAddress() & 255);
        data2[16] = (byte) selectEntity.getLength();
        byte[] selectData = DataConverter.hexToBytes(selectEntity.getData());
        for (int k2 = 0; k2 < len; k2++) {
            data2[k2 + 17] = selectData[k2];
        }
        int k3 = len + 17;
        for (int k4 = 0; k4 < byteData2.length; k4++) {
            data2[k3 + k4] = byteData2[k4];
        }
        return buildSendData(36, data2);
    }

    public UHFReaderResult<Boolean> analysisWriteResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeKillSendData(String password, SelectEntity selectEntity) {
        if (selectEntity == null) {
            byte[] pwd = DataConverter.hexToBytes(password);
            return buildSendData(38, new byte[]{3, -24, 0, pwd[0], pwd[1], pwd[2], pwd[3], 0});
        }
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        byte[] data = new byte[(len + 13)];
        data[0] = 3;
        data[1] = -24;
        data[2] = (byte) selectEntity.getOption();
        byte[] pwd2 = DataConverter.hexToBytes(password);
        data[3] = pwd2[0];
        data[4] = pwd2[1];
        data[5] = pwd2[2];
        data[6] = pwd2[3];
        data[7] = 0;
        data[8] = (byte) ((selectEntity.getAddress() >> 24) & 255);
        data[9] = (byte) ((selectEntity.getAddress() >> 16) & 255);
        data[10] = (byte) ((selectEntity.getAddress() >> 8) & 255);
        data[11] = (byte) (selectEntity.getAddress() & 255);
        data[12] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        for (int k = 0; k < len; k++) {
            data[k + 13] = byteData[k];
        }
        return buildSendData(38, data);
    }

    public UHFReaderResult<Boolean> analysisKillResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeLockSendData(String password, LockMembankEnum membankEnum, LockActionEnum actionEnum, SelectEntity selectEntity) {
        int len;
        byte[] membankByte = new byte[2];
        byte[] actionByte = new byte[2];
        int i = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum[membankEnum.ordinal()];
        if (i == 1) {
            int i2 = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[actionEnum.ordinal()];
            if (i2 == 1) {
                membankByte[0] = 32;
                actionByte[0] = 32;
            } else if (i2 == 2) {
                membankByte[0] = 32;
                actionByte[0] = 0;
            } else if (i2 == 3) {
                membankByte[0] = 48;
                actionByte[0] = 32;
            } else if (i2 == 4) {
                membankByte[0] = 48;
                actionByte[0] = 16;
            }
        } else if (i == 2) {
            int i3 = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[actionEnum.ordinal()];
            if (i3 == 1) {
                membankByte[0] = 8;
                actionByte[0] = 8;
            } else if (i3 == 2) {
                membankByte[0] = 8;
                actionByte[0] = 0;
            } else if (i3 == 3) {
                membankByte[0] = 12;
                actionByte[0] = 12;
            } else if (i3 == 4) {
                membankByte[0] = 12;
                actionByte[0] = 4;
            }
        } else if (i == 3) {
            int i4 = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[actionEnum.ordinal()];
            if (i4 == 1) {
                membankByte[0] = 2;
                actionByte[0] = 2;
            } else if (i4 == 2) {
                membankByte[0] = 2;
                actionByte[0] = 0;
            } else if (i4 == 3) {
                membankByte[0] = 3;
                actionByte[0] = 3;
            } else if (i4 == 4) {
                membankByte[0] = 3;
                actionByte[0] = 1;
            }
        } else if (i == 4) {
            int i5 = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[actionEnum.ordinal()];
            if (i5 == 1) {
                membankByte[1] = 2;
                actionByte[1] = 2;
            } else if (i5 == 2) {
                membankByte[1] = 2;
                actionByte[1] = 0;
            } else if (i5 == 3) {
                membankByte[1] = 3;
                actionByte[1] = 3;
            } else if (i5 == 4) {
                membankByte[1] = 3;
                actionByte[1] = 1;
            }
        } else if (i == 5) {
            int i6 = AnonymousClass1.$SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[actionEnum.ordinal()];
            if (i6 == 1) {
                membankByte[0] = Byte.MIN_VALUE;
                actionByte[0] = Byte.MIN_VALUE;
            } else if (i6 == 2) {
                membankByte[0] = Byte.MIN_VALUE;
                actionByte[0] = 0;
            } else if (i6 == 3) {
                membankByte[0] = -64;
                actionByte[0] = -64;
            } else if (i6 == 4) {
                membankByte[0] = -64;
                actionByte[0] = 64;
            }
        }
        byte temp = membankByte[0];
        membankByte[0] = membankByte[1];
        membankByte[1] = temp;
        byte temp2 = actionByte[0];
        actionByte[0] = actionByte[1];
        actionByte[1] = temp2;
        String hexMask = DataConverter.bytesToHex(membankByte);
        String hexAction = DataConverter.bytesToHex(actionByte);
        LoggerUtils.d(this.TAG, "lock hexMask=" + hexMask);
        LoggerUtils.d(this.TAG, "lock hexAction=" + hexAction);
        if (selectEntity == null) {
            byte[] pwd = DataConverter.hexToBytes(password);
            byte[] byteMask = DataConverter.hexToBytes(hexMask);
            byte[] byteAction = DataConverter.hexToBytes(hexAction);
            return buildSendData(37, new byte[]{3, -24, 0, pwd[0], pwd[1], pwd[2], pwd[3], byteMask[0], byteMask[1], byteAction[0], byteAction[1]});
        }
        int len2 = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len = len2 + 1;
        } else {
            len = len2;
        }
        byte[] data = new byte[(len + 16)];
        data[0] = 3;
        data[1] = -24;
        data[2] = (byte) selectEntity.getOption();
        byte[] pwd2 = DataConverter.hexToBytes(password);
        data[3] = pwd2[0];
        data[4] = pwd2[1];
        data[5] = pwd2[2];
        data[6] = pwd2[3];
        byte[] byteMask2 = DataConverter.hexToBytes(hexMask);
        data[7] = byteMask2[0];
        data[8] = byteMask2[1];
        byte[] byteAction2 = DataConverter.hexToBytes(hexAction);
        data[9] = byteAction2[0];
        data[10] = byteAction2[1];
        data[11] = (byte) ((selectEntity.getAddress() >> 24) & 255);
        data[12] = (byte) ((selectEntity.getAddress() >> 16) & 255);
        data[13] = (byte) ((selectEntity.getAddress() >> 8) & 255);
        data[14] = (byte) (selectEntity.getAddress() & 255);
        data[15] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        for (int k = 0; k < len; k++) {
            data[k + 16] = byteData[k];
        }
        return buildSendData(37, data);
    }

    /* renamed from: com.xlzn.hcpda.uhf.analysis.BuilderAnalysisSLR$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum;
        static final /* synthetic */ int[] $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum;

        static {
            int[] iArr = new int[LockMembankEnum.values().length];
            $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum = iArr;
            try {
                iArr[LockMembankEnum.EPC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum[LockMembankEnum.TID.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum[LockMembankEnum.USER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum[LockMembankEnum.KillPwd.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockMembankEnum[LockMembankEnum.AccessPwd.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            int[] iArr2 = new int[LockActionEnum.values().length];
            $SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum = iArr2;
            try {
                iArr2[LockActionEnum.LOCK.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[LockActionEnum.UNLOCK.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[LockActionEnum.PERMANENT_LOCK.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$xlzn$hcpda$uhf$enums$LockActionEnum[LockActionEnum.PERMANENT_UNLOCK.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public UHFReaderResult<Boolean> analysisLockResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] buildSendData(int cmd, byte[] data) {
        LoggerUtils.d(this.TAG, "组合数据-------" + DataConverter.bytesToHex(data));
        if (data == null || data.length <= 0) {
            byte[] sendData = new byte[5];
            sendData[0] = -1;
            sendData[1] = 0;
            sendData[2] = (byte) cmd;
            byte[] crc = new byte[2];
            ModuleAPI.getInstance().CalcCRC(Arrays.copyOfRange(sendData, 1, 3), 2, crc);
            sendData[3] = crc[0];
            sendData[4] = crc[1];
            LoggerUtils.d(this.TAG, "构建发送数据buildSendData=>" + DataConverter.bytesToHex(sendData));
            return sendData;
        }
        byte[] sendData2 = new byte[(data.length + 5)];
        int index = 0 + 1;
        sendData2[0] = -1;
        int index2 = index + 1;
        sendData2[index] = (byte) data.length;
        int index3 = index2 + 1;
        sendData2[index2] = (byte) cmd;
        int k = 0;
        while (k < data.length) {
            sendData2[index3] = data[k];
            k++;
            index3++;
        }
        byte[] crc2 = new byte[2];
        byte[] check = Arrays.copyOfRange(sendData2, 1, data.length + 3);
        ModuleAPI.getInstance().CalcCRC(check, check.length, crc2);
        int index4 = index3 + 1;
        sendData2[index3] = crc2[0];
        int i = index4 + 1;
        sendData2[index4] = crc2[1];
        LoggerUtils.d(this.TAG, "构建发送数据buildSendData=>" + DataConverter.bytesToHex(sendData2));
        return sendData2;
    }

    public byte[] makeSetBaudRate(int baudrate) {
        return buildSendData(6, new byte[]{(byte) ((baudrate >> 24) & 255), (byte) ((baudrate >> 16) & 255), (byte) ((baudrate >> 8) & 255), (byte) (baudrate & 255)});
    }

    public UHFReaderResult<Boolean> analysisSetBaudRateResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeSetFrequencyPoint(int frequencyPoint) {
        return buildSendData(149, new byte[]{(byte) ((frequencyPoint >> 24) & 255), (byte) ((frequencyPoint >> 16) & 255), (byte) ((frequencyPoint >> 8) & 255), (byte) (frequencyPoint & 255)});
    }

    public UHFReaderResult<Boolean> analysisSetFrequencyPointResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public byte[] makeSetRFLink(int mode) {
        byte[] data = new byte[3];
        data[0] = 5;
        data[1] = 2;
        if (mode == 0) {
            data[2] = 111;
        }
        if (mode == 1) {
            data[2] = 101;
        }
        if (mode == 2) {
            data[2] = 107;
        }
        if (mode == 3) {
            data[2] = 113;
        }
        return buildSendData(155, data);
    }

    public UHFReaderResult<Boolean> analysisSetRFLinkResultData(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return new UHFReaderResult<>(1);
        }
        return new UHFReaderResult<>(0, "", true);
    }

    public List<UHFTagEntity> analysisFastModeTagInfoReceiveDataMoreTag(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo) {
        return null;
    }
}
