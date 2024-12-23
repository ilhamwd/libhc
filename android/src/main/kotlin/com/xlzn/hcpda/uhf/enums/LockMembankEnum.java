package com.xlzn.hcpda.uhf.enums;

public enum LockMembankEnum {
    KillPwd(0),
    AccessPwd(1),
    EPC(2),
    TID(3),
    USER(4);
    
    int lock;

    private LockMembankEnum(int lock2) {
        this.lock = 0;
        this.lock = lock2;
    }

    public int getValue() {
        return this.lock;
    }

    public static LockMembankEnum getValue(int value) {
        if (value == 0) {
            return KillPwd;
        }
        if (value == 1) {
            return AccessPwd;
        }
        if (value == 2) {
            return EPC;
        }
        if (value == 3) {
            return TID;
        }
        if (value != 4) {
            return null;
        }
        return USER;
    }
}
