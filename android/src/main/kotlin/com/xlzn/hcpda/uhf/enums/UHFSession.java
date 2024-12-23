package com.xlzn.hcpda.uhf.enums;

public enum UHFSession {
    S0(0),
    S1(1),
    S2(2),
    S3(3);
    
    int session;

    private UHFSession(int session2) {
        this.session = 0;
        this.session = session2;
    }

    public int getValue() {
        return this.session;
    }

    public static UHFSession getValue(int value) {
        if (value == 0) {
            return S0;
        }
        if (value == 1) {
            return S1;
        }
        if (value == 2) {
            return S2;
        }
        if (value != 3) {
            return null;
        }
        return S3;
    }
}
