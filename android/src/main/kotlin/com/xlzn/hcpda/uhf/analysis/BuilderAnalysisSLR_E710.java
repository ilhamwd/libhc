package com.xlzn.hcpda.uhf.analysis;

import androidx.core.view.InputDeviceCompat;
import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.module.UHFReaderSLR;
import com.xlzn.hcpda.utils.DataConverter;
import com.xlzn.hcpda.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.UByte;

public class BuilderAnalysisSLR_E710 extends BuilderAnalysisSLR {
    private String TAG = "BuilderAnalysisSLR_E710";

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataNeedTid(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID) {
        this.isTID = isTID;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 72) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public byte[] makeStartFastModeInventorySendData(SelectEntity selectEntity, boolean isTID) {
        int index;
        SelectEntity selectEntity2 = selectEntity;
        LoggerUtils.d(this.TAG, "呀是来的这啊 过滤 = " + selectEntity2);
        if (selectEntity2 == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index2 = moduletech.length;
            int subcrcIndex = index2;
            int index3 = index2 + 1;
            senddata[index2] = -86;
            int index4 = index3 + 1;
            senddata[index3] = 72;
            int index5 = index4 + 1;
            senddata[index4] = 0;
            int index6 = index5 + 1;
            senddata[index5] = 2;
            int index7 = index6 + 1;
            senddata[index6] = 0;
            int index8 = index7 + 1;
            senddata[index7] = 0;
            if (!isTID) {
                int index9 = index8 + 1;
                senddata[index8] = 3;
                index = index9 + 1;
                senddata[index9] = -9;
            } else {
                int index10 = index8 + 1;
                senddata[index8] = 4;
                int index11 = index10 + 1;
                senddata[index10] = 1;
                int index12 = index11 + 1;
                senddata[index11] = 9;
                int index13 = index12 + 1;
                senddata[index12] = 40;
                int index14 = index13 + 1;
                senddata[index13] = 0;
                int index15 = index14 + 1;
                senddata[index14] = 0;
                int index16 = index15 + 1;
                senddata[index15] = 0;
                int index17 = index16 + 1;
                senddata[index16] = 2;
                int index18 = index17 + 1;
                senddata[index17] = 0;
                int index19 = index18 + 1;
                senddata[index18] = 0;
                int index20 = index19 + 1;
                senddata[index19] = 0;
                int index21 = index20 + 1;
                senddata[index20] = 0;
                senddata[index21] = 6;
                index = index21 + 1;
            }
            int subcrcTemp = 0;
            for (int k = subcrcIndex; k < index; k++) {
                subcrcTemp += senddata[k] & 0xFF;
            }
            senddata[index] = -69;
            return buildSendData(170, Arrays.copyOf(senddata, index + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index22 = moduletech2.length;
        int subcrcIndex2 = index22;
        int index23 = index22 + 1;
        senddata2[index22] = -86;
        int index24 = index23 + 1;
        senddata2[index23] = 72;
        int index25 = index24 + 1;
        senddata2[index24] = 0;
        int index26 = index25 + 1;
        senddata2[index25] = 7;
        int index27 = index26 + 1;
        senddata2[index26] = (byte) selectEntity.getOption();
        int index28 = index27 + 1;
        senddata2[index27] = 16;
        int index29 = index28 + 1;
        senddata2[index28] = 0;
        int index30 = index29 + 1;
        senddata2[index29] = 0;
        int index31 = index30 + 1;
        senddata2[index30] = 0;
        int index32 = index31 + 1;
        senddata2[index31] = 0;
        int index33 = index32 + 1;
        senddata2[index32] = 0;
        int address = selectEntity.getAddress();
        int index34 = index33 + 1;
        senddata2[index33] = (byte) ((address >> 24) & 255);
        int index35 = index34 + 1;
        senddata2[index34] = (byte) ((address >> 16) & 255);
        int index36 = index35 + 1;
        senddata2[index35] = (byte) ((address >> 8) & 255);
        int index37 = index36 + 1;
        senddata2[index36] = (byte) (address & 255);
        int index38 = index37 + 1;
        senddata2[index37] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k2 = 0;
        while (k2 < len) {
            senddata2[index38] = byteData[k2];
            k2++;
            index38++;
        }
        int subcrcTemp2 = 0;
        for (int k3 = subcrcIndex2; k3 < index38; k3++) {
            subcrcTemp2 += senddata2[k3] & 0xFF;
        }
        int k4 = index38 + 1;
        senddata2[index38] = (byte) (subcrcTemp2 & 255);
        senddata2[k4] = -69;
        return buildSendData(170, Arrays.copyOf(senddata2, k4 + 1));
    }

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataMoreTag(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID) {
        this.isTID = isTID;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "E710开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 88) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public List<UHFTagEntity> analysisFastModeTagInfoReceiveDataMoreTag(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return null;
        }
        LoggerUtils.d(this.TAG, "解析盘点数据：" + DataConverter.bytesToHex(data.data));
        byte[] taginfo = data.data;
        List<UHFTagEntity> list = new ArrayList<>();
        byte rssi = taginfo[2];
        int statIndex = 2 + 1;
        LoggerUtils.d(this.TAG, "解析盘点数据：" + 2);
        int statIndex2 = statIndex + 1;
        int count = taginfo[statIndex] & 255;
        int statIndex3 = statIndex2 + 1;
        int epcLen = taginfo[statIndex2] & 255;
        UHFTagEntity uhfTagEntity = new UHFTagEntity();
        LoggerUtils.d(this.TAG, "解析盘点数据 epcLen：" + epcLen);
        LoggerUtils.d(this.TAG, "解析盘点数据 RSSI：" + rssi);
        LoggerUtils.d(this.TAG, "解析盘点数据 次数：" + count);
        int statIndex4 = statIndex3 + 1;
        int statIndex5 = statIndex4 + 1;
        byte[] pcBytes = {taginfo[statIndex3], taginfo[statIndex4]};
        int epcIdLen = (epcLen - 2) - 2;
        byte[] epcBytes = new byte[epcIdLen];
        int m = 0;
        while (m < epcIdLen) {
            epcBytes[m] = taginfo[statIndex5];
            m++;
            statIndex5++;
        }
        uhfTagEntity.setRssi(rssi);
        uhfTagEntity.setCount(count);
        uhfTagEntity.setEcpHex(DataConverter.bytesToHex(epcBytes));
        if (uhfTagEntity.getEcpHex() == null) {
            uhfTagEntity.setEcpHex("");
        }
        uhfTagEntity.setPcHex(DataConverter.bytesToHex(pcBytes));
        list.add(uhfTagEntity);
        return list;
    }

    public UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo data, boolean isTID) {
        this.isTID = isTID;
        if (data != null && data.status == 0) {
            LoggerUtils.d(this.TAG, "开始盘点指令返回Data:" + DataConverter.bytesToHex(data.data));
            if ((data.data[10] & 0xFF) == 170 && (data.data[11] & 0xFF) == 72) {
                return new UHFReaderResult<>(0, "", true);
            }
        }
        return new UHFReaderResult<>(1, "", false);
    }

    public List<UHFTagEntity> analysisFastModeTagInfoReceiveDataOld(UHFProtocolAnalysisBase.DataFrameInfo data) {
        if (data == null || data.status != 0) {
            return null;
        }
        LoggerUtils.d(this.TAG, "解析盘点数据： = " + DataConverter.bytesToHex(data.data));
        byte[] taginfo = data.data;
        int rssi = taginfo[2] & 255;
        LoggerUtils.d(this.TAG, "信号强度 ： = " + rssi);
        List<UHFTagEntity> list = new ArrayList<>();
        UHFTagEntity uhfTagEntity = new UHFTagEntity();
        int statIndex = 3 + 1;
        int epcLen = taginfo[3] & 255;
        LoggerUtils.d(this.TAG, "解析盘点数据 epcLen：" + epcLen);
        int statIndex2 = statIndex + 1;
        int statIndex3 = statIndex2 + 1;
        byte[] pcBytes = {taginfo[statIndex], taginfo[statIndex2]};
        int epcIdLen = (epcLen - 2) - 2;
        byte[] epcBytes = new byte[epcIdLen];
        int m = 0;
        while (m < epcIdLen) {
            epcBytes[m] = taginfo[statIndex3];
            m++;
            statIndex3++;
        }
        uhfTagEntity.setRssi(rssi + InputDeviceCompat.SOURCE_ANY);
        uhfTagEntity.setCount(1);
        uhfTagEntity.setEcpHex(DataConverter.bytesToHex(epcBytes));
        if (uhfTagEntity.getEcpHex() == null) {
            uhfTagEntity.setEcpHex("");
        }
        uhfTagEntity.setPcHex(DataConverter.bytesToHex(pcBytes));
        list.add(uhfTagEntity);
        return list;
    }

    public byte[] makeStartFastModeInventorySendDataNeedTid(SelectEntity selectEntity, boolean isTID) {
        SelectEntity selectEntity2 = selectEntity;
        LoggerUtils.d(this.TAG, "0000000000000000000000---------------" + selectEntity2);
        if (!UHFReaderSLR.isR2000 && selectEntity2 != null && isTID) {
            return makeStartFastModeInventorySendDataNeedTid3(selectEntity2, true);
        }
        if (selectEntity2 == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index = moduletech.length;
            int index2 = index + 1;
            senddata[index] = -86;
            int index3 = index2 + 1;
            senddata[index2] = 72;
            int index4 = index3 + 1;
            senddata[index3] = 0;
            int index5 = index4 + 1;
            senddata[index4] = -126;
            int index6 = index5 + 1;
            senddata[index5] = 0;
            int index7 = index6 + 1;
            senddata[index6] = 0;
            int index8 = index7 + 1;
            senddata[index7] = 4;
            int index9 = index8 + 1;
            senddata[index8] = 3;
            int index10 = index9 + 1;
            senddata[index9] = 21;
            int index11 = index10 + 1;
            senddata[index10] = 40;
            int index12 = index11 + 1;
            senddata[index11] = 0;
            int index13 = index12 + 1;
            senddata[index12] = 0;
            int index14 = index13 + 1;
            senddata[index13] = 0;
            int index15 = index14 + 1;
            senddata[index14] = 2;
            int index16 = index15 + 1;
            senddata[index15] = 0;
            int index17 = index16 + 1;
            senddata[index16] = 0;
            int index18 = index17 + 1;
            senddata[index17] = 0;
            int index19 = index18 + 1;
            senddata[index18] = 0;
            int index20 = index19 + 1;
            senddata[index19] = 6;
            int index21 = index20 + 1;
            senddata[index20] = 3;
            int index22 = index21 + 1;
            senddata[index21] = 0;
            int index23 = index22 + 1;
            senddata[index22] = 0;
            int index24 = index23 + 1;
            senddata[index23] = 0;
            int index25 = index24 + 1;
            senddata[index24] = 0;
            int index26 = index25 + 1;
            senddata[index25] = 32;
            int index27 = index26 + 1;
            senddata[index26] = 3;
            int index28 = index27 + 1;
            senddata[index27] = 0;
            int index29 = index28 + 1;
            senddata[index28] = 0;
            int index30 = index29 + 1;
            senddata[index29] = 0;
            int index31 = index30 + 1;
            senddata[index30] = 32;
            int index32 = index31 + 1;
            senddata[index31] = 11;
            int subcrcTemp = 0;
            for (int k = index; k < index32; k++) {
                subcrcTemp += senddata[k] & 0xFF;
            }
            int k2 = index32 + 1;
            senddata[index32] = (byte) (subcrcTemp & 255);
            senddata[k2] = -69;
            return buildSendData(170, Arrays.copyOf(senddata, k2 + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index33 = moduletech2.length;
        int subcrcIndex = index33;
        int index34 = index33 + 1;
        senddata2[index33] = -86;
        int index35 = index34 + 1;
        senddata2[index34] = 72;
        int index36 = index35 + 1;
        senddata2[index35] = 0;
        int index37 = index36 + 1;
        senddata2[index36] = 7;
        int index38 = index37 + 1;
        senddata2[index37] = (byte) selectEntity.getOption();
        int index39 = index38 + 1;
        senddata2[index38] = 16;
        int index40 = index39 + 1;
        senddata2[index39] = 0;
        int index41 = index40 + 1;
        senddata2[index40] = 0;
        int index42 = index41 + 1;
        senddata2[index41] = 0;
        int index43 = index42 + 1;
        senddata2[index42] = 0;
        int index44 = index43 + 1;
        senddata2[index43] = 0;
        int address = selectEntity.getAddress();
        int index45 = index44 + 1;
        senddata2[index44] = (byte) ((address >> 24) & 255);
        int index46 = index45 + 1;
        senddata2[index45] = (byte) ((address >> 16) & 255);
        int index47 = index46 + 1;
        senddata2[index46] = (byte) ((address >> 8) & 255);
        int index48 = index47 + 1;
        senddata2[index47] = (byte) (address & 255);
        int index49 = index48 + 1;
        senddata2[index48] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k3 = 0;
        while (k3 < len) {
            senddata2[index49] = byteData[k3];
            k3++;
            index49++;
        }
        int subcrcTemp2 = 0;
        for (int k4 = subcrcIndex; k4 < index49; k4++) {
            subcrcTemp2 += senddata2[k4] & 0xFF;
        }
        int k5 = index49 + 1;
        senddata2[index49] = (byte) (subcrcTemp2 & 255);
        senddata2[k5] = -69;
        return buildSendData(170, Arrays.copyOf(senddata2, k5 + 1));
    }

    public byte[] makeStartFastModeInventorySendDataNeedTid512(SelectEntity selectEntity, boolean isTID) {
        SelectEntity selectEntity2 = selectEntity;
        LoggerUtils.d(this.TAG, "0000000000000000000000---------------" + selectEntity2);
        if (!UHFReaderSLR.isR2000 && selectEntity2 != null && isTID) {
            return makeStartFastModeInventorySendDataNeedTid3(selectEntity2, true);
        }
        if (selectEntity2 == null) {
            byte[] senddata = new byte[512];
            byte[] moduletech = "Moduletech".getBytes();
            System.arraycopy(moduletech, 0, senddata, 0, moduletech.length);
            int index = moduletech.length;
            int index2 = index + 1;
            senddata[index] = -86;
            int index3 = index2 + 1;
            senddata[index2] = 72;
            int index4 = index3 + 1;
            senddata[index3] = 0;
            int index5 = index4 + 1;
            senddata[index4] = -126;
            int index6 = index5 + 1;
            senddata[index5] = 0;
            int index7 = index6 + 1;
            senddata[index6] = 0;
            int index8 = index7 + 1;
            senddata[index7] = 4;
            int index9 = index8 + 1;
            senddata[index8] = 2;
            int index10 = index9 + 1;
            senddata[index9] = 15;
            int index11 = index10 + 1;
            senddata[index10] = 40;
            int index12 = index11 + 1;
            senddata[index11] = 0;
            int index13 = index12 + 1;
            senddata[index12] = 0;
            int index14 = index13 + 1;
            senddata[index13] = 0;
            int index15 = index14 + 1;
            senddata[index14] = 2;
            int index16 = index15 + 1;
            senddata[index15] = 0;
            int index17 = index16 + 1;
            senddata[index16] = 0;
            int index18 = index17 + 1;
            senddata[index17] = 0;
            int index19 = index18 + 1;
            senddata[index18] = 0;
            int index20 = index19 + 1;
            senddata[index19] = 6;
            int index21 = index20 + 1;
            senddata[index20] = 3;
            int index22 = index21 + 1;
            senddata[index21] = 0;
            int index23 = index22 + 1;
            senddata[index22] = 0;
            int index24 = index23 + 1;
            senddata[index23] = 0;
            int index25 = index24 + 1;
            senddata[index24] = 0;
            int index26 = index25 + 1;
            senddata[index25] = 32;
            int subcrcTemp = 0;
            for (int k = index; k < index26; k++) {
                subcrcTemp += senddata[k] & 0xFF;
            }
            int k2 = index26 + 1;
            senddata[index26] = (byte) (subcrcTemp & 255);
            senddata[k2] = -69;
            return buildSendData(170, Arrays.copyOf(senddata, k2 + 1));
        }
        byte[] senddata2 = new byte[512];
        byte[] moduletech2 = "Moduletech".getBytes();
        System.arraycopy(moduletech2, 0, senddata2, 0, moduletech2.length);
        int index27 = moduletech2.length;
        int subcrcIndex = index27;
        int index28 = index27 + 1;
        senddata2[index27] = -86;
        int index29 = index28 + 1;
        senddata2[index28] = 72;
        int index30 = index29 + 1;
        senddata2[index29] = 0;
        int index31 = index30 + 1;
        senddata2[index30] = 7;
        int index32 = index31 + 1;
        senddata2[index31] = (byte) selectEntity.getOption();
        int index33 = index32 + 1;
        senddata2[index32] = 16;
        int index34 = index33 + 1;
        senddata2[index33] = 0;
        int index35 = index34 + 1;
        senddata2[index34] = 0;
        int index36 = index35 + 1;
        senddata2[index35] = 0;
        int index37 = index36 + 1;
        senddata2[index36] = 0;
        int index38 = index37 + 1;
        senddata2[index37] = 0;
        int address = selectEntity.getAddress();
        int index39 = index38 + 1;
        senddata2[index38] = (byte) ((address >> 24) & 255);
        int index40 = index39 + 1;
        senddata2[index39] = (byte) ((address >> 16) & 255);
        int index41 = index40 + 1;
        senddata2[index40] = (byte) ((address >> 8) & 255);
        int index42 = index41 + 1;
        senddata2[index41] = (byte) (address & 255);
        int index43 = index42 + 1;
        senddata2[index42] = (byte) selectEntity.getLength();
        byte[] byteData = DataConverter.hexToBytes(selectEntity.getData());
        int len = selectEntity.getLength() / 8;
        if (selectEntity.getLength() % 8 != 0) {
            len++;
        }
        int k3 = 0;
        while (k3 < len) {
            senddata2[index43] = byteData[k3];
            k3++;
            index43++;
        }
        int subcrcTemp2 = 0;
        for (int k4 = subcrcIndex; k4 < index43; k4++) {
            subcrcTemp2 += senddata2[k4] & 0xFF;
        }
        int k5 = index43 + 1;
        senddata2[index43] = (byte) (subcrcTemp2 & 255);
        senddata2[k5] = -69;
        return buildSendData(170, Arrays.copyOf(senddata2, k5 + 1));
    }
}
