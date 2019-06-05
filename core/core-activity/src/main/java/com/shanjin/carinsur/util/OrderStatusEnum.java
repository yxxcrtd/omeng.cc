package com.shanjin.carinsur.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
public enum OrderStatusEnum {

    ORDER_CREATE(1,"订单创建状态"),ORDER_SUCCESS(2,"订单成功状态"),ORDER_ABANDONED(3,"订单废弃状态"),ORDER_FAIL(4,"订单失败状态"),;

    private int value;
    private String name;

    OrderStatusEnum(int value, String name) {
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
