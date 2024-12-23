package com.dawn.decoderapijni.bean;

import java.io.Serializable;

public class AttrHelpBean implements Serializable {
    private String cnName;
    private String name;
    private int saveValue = 0;
    private String type;
    private String valueText;

    public AttrHelpBean(String name2, String cnName2, String type2, int saveValue2, String valueText2) {
        this.name = name2;
        this.cnName = cnName2;
        this.type = type2;
        this.saveValue = saveValue2;
        this.valueText = valueText2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getCnName() {
        return this.cnName;
    }

    public void setCnName(String cnName2) {
        this.cnName = cnName2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public int getSaveValue() {
        return this.saveValue;
    }

    public void setSaveValue(int saveValue2) {
        this.saveValue = saveValue2;
    }

    public String getValueText() {
        return this.valueText;
    }

    public void setValueText(String valueText2) {
        this.valueText = valueText2;
    }
}
