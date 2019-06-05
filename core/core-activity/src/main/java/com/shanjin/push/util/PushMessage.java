package com.shanjin.push.util;


import com.shanjin.common.util.StringUtil;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PushMessage {

    public static Map<String, Object> getPushMsg(Map<String, Object> configMap, String appType, Long merchantId, Long orderId, int pushType, String data) {
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("appType", appType);
        msg.put("merchantId", merchantId);
        msg.put("orderId", orderId);
        msg.put("pushType", pushType);
        String alertMsg = "";
        if (pushType > 0) {
            alertMsg = (String) configMap.get("pushType-" + pushType);
            if (StringUtils.isEmpty(alertMsg)) {
                if (pushType == 1) {//发送订单
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-1"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "你有一条新订单，请及时查看！";
                    }
                } else if (pushType == 2) {//提供报价方案
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-2"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "服务商已反馈，请及时查看！";
                    }
                } else if (pushType == 3) {//选择报价方案
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-3"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "你已成功获得订单，请为用户提供优质服务！";
                    }
                } else if (pushType == 4) {//已付款
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-4"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "服务完成！";
                    }
                } else if (pushType == 5) {//已评价
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-5"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "用户已对你进行评价，请查看！";
                    }
                } else if (pushType == 6) {//删除员工
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-6"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "你已被老板删除！";
                    }
                } else if (pushType == 7) {//token失效
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-7"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "令牌已经失效，请重新登录！";
                    }
                } else if (pushType == 8) {//多终端登录
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-8"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "你的账号已在别处登录！";
                    }
                } else if (pushType == 9) {//认证通过
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-9"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "认证通过！";
                    }
                } else if (pushType == 10) {//认证未通过！
                    alertMsg = StringUtil.null2Str(configMap.get("pushType-10"));
                    if (StringUtil.isEmpty(alertMsg)) {
                        alertMsg = "认证未通过！";
                    }
                } else if (pushType == 11) {//剪彩！
                    if (StringUtil.isEmpty(data)) {
                        alertMsg = "有好友为你剪彩，请查看！";
                    } else {
                        alertMsg = data;
                    }

                } else {
                    alertMsg = "你有一条新订单，请及时查看！";
                }
            }
        }
        msg.put("alertMsg", alertMsg);
        return msg;
    }
}
