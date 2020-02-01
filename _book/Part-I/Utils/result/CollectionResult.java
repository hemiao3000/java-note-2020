package com.softeem.util;

import java.util.Collection;

public class CollectionResult <T> implements BaseResult<T> {

    protected int code;

    protected String message;

    protected Collection<T> data;

    private CollectionResult() {
        // 构造方法私有
    }

    protected CollectionResult(int code, String message, Collection<T> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T>
    CollectionResult<T> success(Collection<T> data) {
        return new CollectionResult<>(200, null, data);
    }

    public static <T>
    CollectionResult<T> failure(Error error) {
        return new CollectionResult<>(error.getCode(), error.getMessage(), null);
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Collection<T> getDataCollection() {
        return data;
    }

}

