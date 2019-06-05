package com.shanjin.carinsur.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
public enum VoucherTypeEnum {

    FIXED_AMOUNT_RETURN_VOUCHER(1,"固定金额返现券"),;

    private int value;
    private String name;

    VoucherTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
