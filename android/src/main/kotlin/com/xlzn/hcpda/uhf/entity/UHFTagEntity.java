package com.xlzn.hcpda.uhf.entity;

import java.math.BigInteger;

public class UHFTagEntity implements Comparable<UHFTagEntity> {
    private int ant;
    private int count;
    private String ecpHex;
    private String pcHex;
    private int rssi;
    private String tidHex;
    private String userHex;

    public String getUserHex() {
        return this.userHex;
    }

    public void setUserHex(String userHex2) {
        this.userHex = userHex2;
    }

    public String getEcpHex() {
        return this.ecpHex;
    }

    public void setEcpHex(String ecpHex2) {
        this.ecpHex = ecpHex2;
    }

    public String getPcHex() {
        return this.pcHex;
    }

    public void setPcHex(String pcHex2) {
        this.pcHex = pcHex2;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi2) {
        this.rssi = rssi2;
    }

    public int getAnt() {
        return this.ant;
    }

    public void setAnt(int ant2) {
        this.ant = ant2;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count2) {
        this.count = count2;
    }

    public String getTidHex() {
        return this.tidHex;
    }

    public void setTidHex(String tidHex2) {
        this.tidHex = tidHex2;
    }

    public int compareTo(UHFTagEntity o) {
        return new BigInteger(getEcpHex(), 16).compareTo(new BigInteger(o.getEcpHex(), 16));
    }
}
