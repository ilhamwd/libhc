package com.xlzn.hcpda.uhf.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class UHFProtocolAnalysisBase {
    public List<DataFrameInfo> listCmd = new ArrayList();
    public LinkedBlockingQueue<DataFrameInfo> queueTaginfo = new LinkedBlockingQueue<>(2000);

    public static class DataFrameInfo {
        public int command;
        public byte[] data;
        public int status;
        public long time;
    }

    public DataFrameInfo getTagInfo() {
        return this.queueTaginfo.poll();
    }

    public void cleanTagInfo() {
        this.queueTaginfo.clear();
    }
}
