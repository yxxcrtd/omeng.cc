package com.shanjin.carinsur.service;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/9
 * @desc 车险推送服务
 */
public interface CarInsurPushService {

    /**
     * 车险订单推送
     * @param orderNo
     */
    void pushOrderSuccess(String orderNo);
}
