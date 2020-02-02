package com.softeem.util;

public class ErrorResult implements BaseResult {

    private Error error;

    public ErrorResult(Error error) {
        this.error = error;
    }

    @Override
    public int getCode() {
        return error.getCode();
    }

    @Override
    public String getMessage() {
        return error.getMessage();
    }
}
