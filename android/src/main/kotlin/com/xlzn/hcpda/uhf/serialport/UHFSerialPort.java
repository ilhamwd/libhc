package com.xlzn.hcpda.uhf.serialport;

import com.xlzn.hcpda.SerialPort;
import com.xlzn.hcpda.uhf.interfaces.IUHFProtocolAnalysis;
import com.xlzn.hcpda.utils.DataConverter;
import com.xlzn.hcpda.utils.LoggerUtils;

public class UHFSerialPort {
    private static UHFSerialPort uhfSerialPort = new UHFSerialPort();
    /* access modifiers changed from: private */
    public String TAG = "UHFSerialPort";
    /* access modifiers changed from: private */
    public IUHFProtocolAnalysis iuhfProtocolAnalysis = null;
    private ReadThread readThread = null;
    private SerialPort serialPort = new SerialPort();
    private int uart_fd = -1;

    public static UHFSerialPort getInstance() {
        return uhfSerialPort;
    }

    public void setIUHFProtocolAnalysis(IUHFProtocolAnalysis iuhfProtocolAnalysis2) {
        this.iuhfProtocolAnalysis = iuhfProtocolAnalysis2;
    }

    public boolean open(String uart, int baudrate, int databits, int stopbits, int parity, IUHFProtocolAnalysis iuhfProtocolAnalysis2) {
        this.iuhfProtocolAnalysis = iuhfProtocolAnalysis2;
        int open = this.serialPort.open(uart, baudrate, databits, stopbits, parity);
        this.uart_fd = open;
        if (open < 0) {
            return false;
        }
        startThread();
        return true;
    }

    public boolean open(String uart, IUHFProtocolAnalysis iuhfProtocolAnalysis2, int baudrate) {
        return open(uart, baudrate, 8, 1, 0, iuhfProtocolAnalysis2);
    }

    public boolean open(String uart, IUHFProtocolAnalysis iuhfProtocolAnalysis2) {
        return open(uart, 115200, 8, 1, 0, iuhfProtocolAnalysis2);
    }

    public boolean close() {
        this.serialPort.close(this.uart_fd);
        stopThread();
        return true;
    }

    public boolean send(byte[] data) {
        if (LoggerUtils.isDebug()) {
            LoggerUtils.d(this.TAG, "发送数据：" + DataConverter.bytesToHex(data));
        }
        return this.serialPort.send(this.uart_fd, data);
    }

    public byte[] receive() {
        return this.serialPort.receive(this.uart_fd);
    }

    private void startThread() {
        if (this.readThread == null) {
            ReadThread readThread2 = new ReadThread();
            this.readThread = readThread2;
            readThread2.start();
        }
    }

    private void stopThread() {
        ReadThread readThread2 = this.readThread;
        if (readThread2 != null) {
            readThread2.stopThread();
            this.readThread = null;
        }
    }

    class ReadThread extends Thread {
        private boolean isSop = false;
        Object lock = new Object();

        ReadThread() {
        }

        public void run() {
            while (!this.isSop) {
                byte[] data = UHFSerialPort.this.receive();
                if (data == null) {
                    synchronized (this.lock) {
                        try {
                            this.lock.wait(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (LoggerUtils.isDebug()) {
                        LoggerUtils.d(UHFSerialPort.this.TAG, "接收数据 data=>" + DataConverter.bytesToHex(data));
                    }
                    if (UHFSerialPort.this.iuhfProtocolAnalysis != null) {
                        UHFSerialPort.this.iuhfProtocolAnalysis.analysis(data);
                    }
                }
            }
        }

        public void stopThread() {
            this.isSop = true;
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }
    }
}
