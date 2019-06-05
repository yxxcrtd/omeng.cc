package com.shanjin.carinsur.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 车险券状态枚举
 */
public enum UserVouStatusEnum {

    NOT_USED(0,"未使用"), HAS_USED(1,"已使用"), HAS_EXPIRED(-1,"未使用");

    private int value;
    private String desc;

    UserVouStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
