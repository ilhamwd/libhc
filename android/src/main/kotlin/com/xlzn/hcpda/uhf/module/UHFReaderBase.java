package com.xlzn.hcpda.uhf.module;

import com.xlzn.hcpda.uhf.enums.ConnectState;

public abstract class UHFReaderBase {
    private ConnectState connectState = ConnectState.DISCONNECT;

    /* access modifiers changed from: protected */
    public void setConnectState(ConnectState connectState2) {
        this.connectState = connectState2;
    }

    /* access modifiers changed from: protected */
    public ConnectState getConnectState() {
        return this.connectState;
    }
}
