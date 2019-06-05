package com.shanjin.common;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.controller.AppOrderSvrManager;

/**
 * 业务MQ发送工具
 */
public class MQTools {

    /**
     * 发送MQ消息
     *
     * @param jsonObject 包含消息体
     * @param orderId 业务ID
     */
    public static void sendMQ(JSONObject jsonObject, Long orderId) throws Exception {

        // Wallet
        if (null != jsonObject.get("msg1")) {
            String msg1 = String.valueOf(jsonObject.get("msg1"));
            try {
                AppOrderSvrManager.getNewOrderService().writeToMQ("opay.orderExchange", msg1);
                BusinessUtil.writeLog("wallet_order_mq_success", "订单ID:" + orderId + "，消息体：" + msg1);
            } catch (Exception e) {
                AppOrderSvrManager.getNewOrderService().insertMQSendFailure("opay.orderExchange", msg1, orderId);
                BusinessUtil.writeLog("wallet_order_mq_failure", "订单ID:" + orderId + "，消息体：" + msg1);
            }
        }

        // C_PLAN
        if (null != jsonObject.get("msg2")) {
            String msg2 = String.valueOf(jsonObject.get("msg2"));
            try {
                AppOrderSvrManager.getNewOrderService().writeToMQ("cplanOrderPayExchange", msg2);
                BusinessUtil.writeLog("c_plan_order_mq_success", "订单ID:" + orderId + "，消息体：" + msg2);
            } catch (Exception e) {
                AppOrderSvrManager.getNewOrderService().insertMQSendFailure("cplanOrderPayExchange", msg2, orderId);
                BusinessUtil.writeLog("c_plan_order_mq_failure", "订单ID:" + orderId + "，消息体：" + msg2);
            }
        }
    }

}
