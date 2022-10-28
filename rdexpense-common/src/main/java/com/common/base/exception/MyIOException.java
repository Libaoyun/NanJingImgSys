package com.common.base.exception;

/**
 * @Classname IoException
 * @Description
 * @Date 2019/5/10 9:20
 * @Created by wangzh
 */
public class MyIOException extends Exception {
    private Integer errorCode;

    public MyIOException() {}

    public MyIOException(Throwable cause) {
        super(cause);
    }

    public MyIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyIOException(String message) {
        super(message);
    }

    public MyIOException(String errorCode, String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

    public MyIOException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

}
