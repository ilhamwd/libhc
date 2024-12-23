package com.xlzn.hcpda.uhf.entity;

public class UHFReaderResult<T> {
    private T data;
    private String message;
    private int resultCode;

    public class ResultCode {
        public static final int CODE_FAILURE = 1;
        public static final int CODE_OPEN_SERIAL_PORT_FAILURE = 3;
        public static final int CODE_POWER_ON_FAILURE = 4;
        public static final int CODE_READER_NOT_CONNECTED = 2;
        public static final int CODE_SUCCESS = 0;

        public ResultCode() {
        }
    }

    public class ResultMessage {
        public static final String CODE_POWER_ON_FAILURE = "模块上电失败!";
        public static final String OPEN_SERIAL_PORT_FAILURE = "打开串口失败.";
        public static final String READER_NOT_CONNECTED = "没有连接UHF模块.";

        public ResultMessage() {
        }
    }

    public UHFReaderResult(int resultCode2) {
        this.resultCode = resultCode2;
    }

    public UHFReaderResult(int resultCode2, String message2) {
        this.resultCode = resultCode2;
        this.message = message2;
    }

    public UHFReaderResult(int resultCode2, String msg, T data2) {
        this.resultCode = resultCode2;
        this.message = this.message;
        this.data = data2;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(int resultCode2) {
        this.resultCode = resultCode2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data2) {
        this.data = data2;
    }
}
