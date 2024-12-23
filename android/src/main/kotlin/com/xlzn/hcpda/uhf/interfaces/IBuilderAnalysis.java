package com.xlzn.hcpda.uhf.interfaces;

import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;
import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.entity.UHFVersionInfo;
import com.xlzn.hcpda.uhf.enums.LockActionEnum;
import com.xlzn.hcpda.uhf.enums.LockMembankEnum;
import com.xlzn.hcpda.uhf.enums.UHFSession;
import java.util.List;

public interface IBuilderAnalysis {
    List<UHFTagEntity> analysisFastModeTagInfoReceiveData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    List<UHFTagEntity> analysisFastModeTagInfoReceiveDataMoreTag(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    List<UHFTagEntity> analysisFastModeTagInfoReceiveDataOld(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Integer> analysisGetFrequencyRegionResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Integer> analysisGetPowerResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<UHFSession> analysisGetSessionResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<int[]> analysisGetTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Integer> analysisGetTemperatureResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisInventorySelectEntityResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisKillResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisLockResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<String> analysisReadResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetBaudRateResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetDynamicTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetFrequencyPointResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetFrequencyRegionResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetPowerResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetRFLinkResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetSessionResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisSetStaticTargetResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<UHFTagEntity> analysisSingleTagInventoryResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo, boolean z);

    UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataNeedTid(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo, boolean z);

    UHFReaderResult<Boolean> analysisStartFastModeInventoryReceiveDataNeedTidMoreTag(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo, boolean z);

    UHFReaderResult<Integer> analysisStartInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisStopFastModeInventoryReceiveData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    List<UHFTagEntity> analysisTagInfoReceiveData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<UHFVersionInfo> analysisVersionData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    UHFReaderResult<Boolean> analysisWriteResultData(UHFProtocolAnalysisBase.DataFrameInfo dataFrameInfo);

    byte[] makeGetFrequencyRegionSendData();

    byte[] makeGetPowerSendData();

    byte[] makeGetSessionSendData();

    byte[] makeGetTagInfoSendData();

    byte[] makeGetTargetSendData();

    byte[] makeGetTemperatureSendData();

    byte[] makeGetVersionSendData();

    byte[] makeInventorySelectEntity(SelectEntity selectEntity);

    byte[] makeKillSendData(String str, SelectEntity selectEntity);

    byte[] makeLockSendData(String str, LockMembankEnum lockMembankEnum, LockActionEnum lockActionEnum, SelectEntity selectEntity);

    byte[] makeReadSendData(String str, int i, int i2, int i3, SelectEntity selectEntity);

    byte[] makeSetBaudRate(int i);

    byte[] makeSetDynamicTargetSendData(int i);

    byte[] makeSetFrequencyPoint(int i);

    byte[] makeSetFrequencyRegionSendData(int i);

    byte[] makeSetPowerSendData(int i);

    byte[] makeSetRFLink(int i);

    byte[] makeSetSessionSendData(UHFSession uHFSession);

    byte[] makeSetStaticTargetSendData(int i);

    byte[] makeSetTargetModel(int i);

    byte[] makeSingleTagInventorySendData(SelectEntity selectEntity);

    byte[] makeStartFastModeInventorySendData(SelectEntity selectEntity, boolean z);

    byte[] makeStartFastModeInventorySendDataMoreTag(SelectEntity selectEntity, boolean z);

    byte[] makeStartFastModeInventorySendDataNeedTid(SelectEntity selectEntity, boolean z);

    byte[] makeStartFastModeInventorySendDataNeedTid512(SelectEntity selectEntity, boolean z);

    byte[] makeStartInventorySendData(SelectEntity selectEntity, boolean z);

    byte[] makeStopFastModeInventorySendData();

    byte[] makeWriteSendData(String str, int i, int i2, int i3, String str2, SelectEntity selectEntity);
}
