package com.dawn.decoderapijni.bean;

public class CodeEnableBean {
    String attrName;
    String attrNickName;
    String attrType;
    String codeName;
    String codeType;
    String enableValue;
    String fullCodeName;
    String propNote;

    public CodeEnableBean(String codeName2, String fullCodeName2, String codeType2, String attrName2, String attrNickName2, String attrType2, String enableValue2, String propNote2) {
        this.codeName = codeName2;
        this.fullCodeName = fullCodeName2;
        this.codeType = codeType2;
        this.attrName = attrName2;
        this.attrNickName = attrNickName2;
        this.attrType = attrType2;
        this.enableValue = enableValue2;
        this.propNote = propNote2;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public void setCodeName(String codeName2) {
        this.codeName = codeName2;
    }

    public String getFullCodeName() {
        return this.fullCodeName;
    }

    public void setFullCodeName(String fullCodeName2) {
        this.fullCodeName = fullCodeName2;
    }

    public String getCodeType() {
        return this.codeType;
    }

    public void setCodeType(String codeType2) {
        this.codeType = codeType2;
    }

    public String getAttrName() {
        return this.attrName;
    }

    public void setAttrName(String attrName2) {
        this.attrName = attrName2;
    }

    public String getAttrNickName() {
        return this.attrNickName;
    }

    public void setAttrNickName(String attrNickName2) {
        this.attrNickName = attrNickName2;
    }

    public String getAttrType() {
        return this.attrType;
    }

    public void setAttrType(String attrType2) {
        this.attrType = attrType2;
    }

    public String getEnableValue() {
        return this.enableValue;
    }

    public void setEnableValue(String enableValue2) {
        this.enableValue = enableValue2;
    }

    public String getPropNote() {
        return this.propNote;
    }

    public void setPropNote(String propNote2) {
        this.propNote = propNote2;
    }
}
