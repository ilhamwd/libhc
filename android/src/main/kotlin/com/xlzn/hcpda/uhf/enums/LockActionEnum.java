package com.xlzn.hcpda.uhf.enums;

public enum LockActionEnum {
    LOCK(0),
    UNLOCK(1),
    PERMANENT_LOCK(2),
    PERMANENT_UNLOCK(3);
    
    int action;

    private LockActionEnum(int action2) {
        this.action = 0;
        this.action = action2;
    }

    public int getValue() {
        return this.action;
    }

    public static LockActionEnum getValue(int value) {
        if (value == 0) {
            return LOCK;
        }
        if (value == 1) {
            return UNLOCK;
        }
        if (value == 2) {
            return PERMANENT_LOCK;
        }
        if (value != 3) {
            return null;
        }
        return PERMANENT_UNLOCK;
    }
}
