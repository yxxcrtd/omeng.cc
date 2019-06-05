package com.shanjin.carinsur.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
public enum BizResultEnum {

    NO_DUE("00","未处理"),NEED_DUE("01","待处理"),SUCCESS_DUE("02","业务处理成功");


    private String value;
    private String desc;

    BizResultEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
