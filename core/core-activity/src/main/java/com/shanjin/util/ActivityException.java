package com.shanjin.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc 活动通用异常
 */
public class ActivityException extends RuntimeException{

    private String errorCode;

    public ActivityException(String errorCode,String msg){
        super(msg);
        this.errorCode = errorCode;
    }
    public ActivityException(String errorCode,Throwable throwable){
        super(throwable);
        this.errorCode = errorCode;
    }
    public ActivityException(String errorCode,String msg,Throwable throwable){
        super(msg,throwable);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}
