package com.shanjin.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 回调头信息枚举类
 */
public enum CallBackHeaderEnum {

    VERSION("version","1.0"),
    SENDTIME("sendTime"),
    AGENTCODE("agentCode","W02412603"),
    SELLERID("sellerId",""),
    PARTNERORDERNO("partnerOrderNo"),
    REQUESTTYPE("requestType");

    private String headName;
    private String defaultValue;

     CallBackHeaderEnum(String headName){
        this.headName = headName;
    }
     CallBackHeaderEnum(String headName,String defaultValue){
         this.headName = headName;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getHeadName() {
        return headName;
    }
}
