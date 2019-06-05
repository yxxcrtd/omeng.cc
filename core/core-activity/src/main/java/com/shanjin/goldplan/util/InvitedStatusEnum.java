package com.shanjin.goldplan.util;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc TODO
 */
public enum InvitedStatusEnum {
    UN_EFFECTIVE(0), EFFECTIVE(1),;

    private int status;

    InvitedStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
