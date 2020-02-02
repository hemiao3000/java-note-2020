package com.softeem.util;

public class SingleResult <T> implements BaseResult<T> {

    private int code;
    private String message;
    private T data;

    private SingleResult() {
        // 构造方法私有
    }

    private SingleResult(int code, String message, T data) {
        this.code  = code;
        this.message = message;
        this.data = data;
    }

    public static <T>
    SingleResult<T> success(T data) {
        return new SingleResult<>(200, null, data);
    }

    public static <T>
    SingleResult<T> failure(Error error) {
        return new SingleResult<>(error.getCode(), error.getMessage(), null);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public T getSingleData() {
        return data;
    }

}

