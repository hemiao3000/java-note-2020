package com.softeem.util;

import java.io.Serializable;
import java.util.Collection;

public interface BaseResult<T> extends Serializable {

    int getCode();

    String getMessage();

}

