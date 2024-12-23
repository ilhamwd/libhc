package com.dawn.decoderapijni;

public class DLException extends Exception {
    private int code;
    private String reasonPhrase;

    public DLException(int code2, String reasonPhrase2) {
        this.reasonPhrase = reasonPhrase2;
        this.code = code2;
    }

    public DLException(int code2, String reasonPhrase2, String message) {
        super(message);
        this.reasonPhrase = reasonPhrase2;
        this.code = code2;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase2) {
        this.reasonPhrase = reasonPhrase2;
    }
}
