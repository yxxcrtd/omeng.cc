package com.shanjin.carinsur.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 订单业务类型枚举
 */
public enum BizTypeEnum {
    NO_BIZ("00", ""), CI_RETURN_VOUCHER("01", "车险券返现业务");

    private String value;
    private String name;

    BizTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    public static BizTypeEnum getBizTypeEnum(String value) {
        for (BizTypeEnum bte : BizTypeEnum.values()) {
            if (bte.getValue().equals(value)) {
                return bte;
            }
        }
        return null;
    }
}
