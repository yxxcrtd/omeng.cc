package com.shanjin.mongo.dao;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface IOrderDao {

    /**
     * 获取用户侧订单列表
     *
     * @param param
     * @return
     */
    JSONObject getOrderListForSender(Map<String, Object> param);


    /**
     * 查询是否存在用户侧历史订单
     *
     * @param param
     * @return
     */
    boolean hasUserOrder(Map<String, Object> param);

    /**
     * 获取用户某订单的明细
     *
     * @param param
     * @return
     */
    JSONObject getOrderDetailForSender(Long orderId, Long userId);


    /**
     * 获取某报价方案详请
     *
     * @param merchantPlanId
     * @return
     */
    JSONObject getMerchantPlanDetail(Long merchantPlanId);


    /**
     * 获取订单详情
     *
     * @param userId
     * @param orderId
     * @return
     */
    Map<String, Object> getOriginalOrderDetail(Long userId, Long orderId);


    JSONObject getOrderListForReceiver(Map<String, Object> map);

    JSONObject getOrderDetailForReceiver(String orderId, Long userId);

}
