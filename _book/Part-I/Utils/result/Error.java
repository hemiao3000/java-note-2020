package com.softeem.util;

public enum Error {

    XXX_ERROR(400,"..."),

    ILLEGAL_YYY(401,"...");

    private int code;
    private String msg;

    Error(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }
}
