package com.shanjin.bean;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/2
 * @desc messageConveter 统一处理数据 模型
 */
public class CommonVo {

    private Long userId;
    private String bizNo;
    private String bizType;


    private Integer vouType;
    private Integer status;

    public Integer getVouType() {
        return vouType;
    }

    public void setVouType(Integer vouType) {
        this.vouType = vouType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }
}
