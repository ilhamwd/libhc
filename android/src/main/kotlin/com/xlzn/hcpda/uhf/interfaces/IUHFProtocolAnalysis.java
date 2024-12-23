package com.xlzn.hcpda.uhf.interfaces;

import com.xlzn.hcpda.uhf.analysis.UHFProtocolAnalysisBase;

public interface IUHFProtocolAnalysis {
    void analysis(byte[] bArr);

    void cleanTagInfo();

    UHFProtocolAnalysisBase.DataFrameInfo getOtherInfo(int i, int i2);

    UHFProtocolAnalysisBase.DataFrameInfo getTagInfo();

    void setCheckCodeErrorCallback(IUHFCheckCodeErrorCallback iUHFCheckCodeErrorCallback);
}
