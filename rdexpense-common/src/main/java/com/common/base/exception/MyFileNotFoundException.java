package com.common.base.exception;

/**
 * @Classname FileNotFoundException
 * @Description
 * @Date 2019/5/10 9:19
 * @Created by wangzh
 */
public class MyFileNotFoundException extends Exception {
    private static final long serialVersionUID = 6958499248468627021L;

    private Integer errorCode;

    public MyFileNotFoundException() {}

    public MyFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public MyFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyFileNotFoundException(String message) {
        super(message);
    }

    public MyFileNotFoundException(String errorCode, String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

    public MyFileNotFoundException(String message, Integer errorCode) {
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
