package com.shanjin.common.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/3
 * @desc 通用restapi 结果vo
 */
public class CommonResultVo<T> {

    private String message;  // 000 成功 00x 标识失败响应码
    private String resultCode;
    private T data;

    public CommonResultVo() {
        this.resultCode = "000";
    }

    public CommonResultVo(String resultCode) {
        this.resultCode = resultCode;
    }

    public CommonResultVo(String resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
