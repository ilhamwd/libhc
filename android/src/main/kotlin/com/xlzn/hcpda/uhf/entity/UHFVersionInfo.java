package com.xlzn.hcpda.uhf.entity;

public class UHFVersionInfo {
    private String firmwareVersion;
    private String hardwareVersion;

    public String getHardwareVersion() {
        return this.hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion2) {
        this.hardwareVersion = hardwareVersion2;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion2) {
        this.firmwareVersion = firmwareVersion2;
    }
}
