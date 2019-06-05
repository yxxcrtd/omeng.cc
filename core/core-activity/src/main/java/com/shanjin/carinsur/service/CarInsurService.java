package com.shanjin.carinsur.service;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 车险服务
 */
public interface CarInsurService {


    /**
     * 创建车险订单
     * @param userId 用户Id
     * @param bizType 业务枚举类value
     * @param bizNo 业务号
     * @return 车险订单编号 --唯一值
     */
    String createOrder(Long userId, String bizType, String bizNo);

    /**
     * 车险回调后处理
     * @param orderNo
     * @return
     */
    void postDueCallBack(String orderNo);

    /**
     * 基于业务查找淡定编号
     */
    String getOrderByBiz(Long userId, String bizNo, String bizType);
}
