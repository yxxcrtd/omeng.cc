package com.shanjin.bean;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
public class ResultVo<T> {

    private String isSuccess;

    private int errorCode;

    private String errorReason;



    public ResultVo(String isSuccess, int errorCode, String errorReason) {
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
        this.errorReason = errorReason;
    }

    public ResultVo(String isSuccess) {
        this.isSuccess = isSuccess;
    }



    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }


    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
