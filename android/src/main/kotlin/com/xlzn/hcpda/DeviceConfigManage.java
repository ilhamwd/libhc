package com.xlzn.hcpda;

import android.os.Build;
import com.xlzn.hcpda.utils.LoggerUtils;

public class DeviceConfigManage {
    private static DeviceConfigManage deviceConfigInfo = new DeviceConfigManage();
    public static String module_type = "R2000";
    private String TAG = "DeviceConfigManage";
    private String model = Build.MODEL;
    private Platform platform;
    private UHFConfig uhfConfig = null;
    private String uhfUart;

    public enum Platform {
        MTK,
        QUALCOMM
    }

    public static DeviceConfigManage getInstance() {
        return deviceConfigInfo;
    }

    private DeviceConfigManage() {
        LoggerUtils.d(this.TAG, " model=" + this.model + " Build.DISPLAY=" + Build.DISPLAY);
        this.uhfUart = "/dev/ttysWK0";
        LoggerUtils.d(this.TAG, " 获取最终串口" + this.uhfUart);
    }

    private static class ConfigBase {
        public String model;
        public Platform platform;

        private ConfigBase() {
        }

        public String getModel() {
            return this.model;
        }

        private void setModel(String model2) {
            this.model = model2;
        }

        public Platform getPlatform() {
            return this.platform;
        }

        private void setPlatform(Platform platform2) {
            this.platform = platform2;
        }
    }

    public static class UHFConfig extends ConfigBase {
        /* access modifiers changed from: private */
        public String uhfUart;

        public UHFConfig() {
            super();
        }

        public /* bridge */ /* synthetic */ String getModel() {
            return super.getModel();
        }

        public /* bridge */ /* synthetic */ Platform getPlatform() {
            return super.getPlatform();
        }

        public String getUhfUart() {
            return this.uhfUart;
        }

        public void setUhfUart(String uhfUart2) {
            this.uhfUart = uhfUart2;
        }
    }

    public UHFConfig getUhfConfig() {
        if (this.uhfConfig == null) {
            UHFConfig uHFConfig = new UHFConfig();
            this.uhfConfig = uHFConfig;
            String unused = uHFConfig.uhfUart = this.uhfUart;
            this.uhfConfig.model = this.model;
            this.uhfConfig.platform = this.platform;
        }
        return this.uhfConfig;
    }
}
