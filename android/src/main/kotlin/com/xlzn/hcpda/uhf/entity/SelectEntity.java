package com.xlzn.hcpda.uhf.entity;

public class SelectEntity {
    public static final int OPTION_EPC = 4;
    public static final int OPTION_TID = 2;
    public static final int OPTION_USER = 3;
    private int address;
    private String data;
    private int length;
    private int option;

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int address2) {
        this.address = address2;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length2) {
        this.length = length2;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data2) {
        this.data = data2;
    }

    public int getOption() {
        return this.option;
    }

    public void setOption(int option2) {
        this.option = option2;
    }
}
