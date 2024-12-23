package com.xlzn.hcpda;

import com.xlzn.hcpda.utils.LoggerUtils;
import java.util.Arrays;

public class SerialPort {
    String TAG = "SerialPort";
    private byte[] receiveData = new byte[1024];
    private byte zero = 0;

    public int open(String uart, int baudrate, int databits, int stopbits, int parity) {
        LoggerUtils.d(this.TAG, " uart=" + uart + " baudrate=" + baudrate + "  databits=" + databits + " stopbits=" + stopbits + " parity=" + parity);
        return ModuleAPI.getInstance().SerailOpen(uart, baudrate, databits, stopbits, parity);
    }

    public boolean close(int uart_fd) {
        LoggerUtils.d(this.TAG, " close uart_fd= " + uart_fd);
        ModuleAPI.getInstance().SerailClose(uart_fd);
        return true;
    }

    public boolean send(int uart_fd, byte[] data) {
        return uart_fd >= 0 && ModuleAPI.getInstance().SerailSendData(uart_fd, data, data.length) == data.length;
    }

    public byte[] receive(int uart_fd) {
        if (uart_fd < 0) {
            return null;
        }
        Arrays.fill(this.receiveData, this.zero);
        ModuleAPI instance = ModuleAPI.getInstance();
        byte[] bArr = this.receiveData;
        int len = instance.SerailReceive(uart_fd, bArr, bArr.length);
        if (len <= 0) {
            return null;
        }
        return Arrays.copyOf(this.receiveData, len);
    }
}
