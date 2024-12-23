package com.dawn.decoderapijni.bean;

import java.io.Serializable;

public class PropValueHelpBean implements Serializable {
    private String cnName;
    private int value;

    public PropValueHelpBean(int value2, String cnName2) {
        this.value = value2;
        this.cnName = cnName2;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value2) {
        this.value = value2;
    }

    public String getCnName() {
        return this.cnName;
    }

    public void setCnName(String cnName2) {
        this.cnName = cnName2;
    }
}
